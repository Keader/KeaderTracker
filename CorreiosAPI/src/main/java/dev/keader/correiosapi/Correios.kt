package dev.keader.correiosapi

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://www2.correios.com.br/sistemas/rastreamento/resultado_semcontent.cfm"
const val BASE_URL_SITE = "https://www2.correios.com.br/sistemas/rastreamento/ctrl/ctrlRastreamento.cfm?"
const val LOCALE_REGEX = "[[A-Z]+\\s*]+/[\\s*[A-Z]+]*"
const val CODE_VALIDATION_REGEX = "^[A-Z]{2}[0-9]{9}[A-Z]{2}"
const val ERROR_MESSAGE = "Não é possível exibir informações para o código informado."
const val UNKNOWN_LOCATION = "LIMBO, DESCONHECIDO"
const val UNKNOWN_TYPE = "DESCONHECIDO"
const val STATUS_WAITING = "Aguardando postagem pelo remetente."

class Correios {
    companion object {
        private val localeRegex = Regex(LOCALE_REGEX)
        private val codeValidation = Regex(CODE_VALIDATION_REGEX)
        private val client = OkHttpClient.Builder()
            .followRedirects(true)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cookieJar(MemoryCookieJar())
            .build()

        /*suspend fun getTrackFromAPI(code: String): CorreiosItem {
            val productCode = code.toUpperCase()

            if (!isValidCode(productCode)) throw IOException("Invalid Code")

            val formBody = FormBody.Builder()
                .add("objetos", productCode)
                .build()

            val request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val response = client.newCall(request).executeSuspend()
            if (!response.isSuccessful) throw IOException("Response: ${response.code} for code: $code")

            val tracks = mutableListOf<CorreiosTrack>()
            val document = Jsoup.parse(response.body!!.string())
            val html = document.html()
            if (html.contains(ERROR_MESSAGE) || html.contains(STATUS_WAITING)) {
                return handleWithNotPosted(productCode)
            }

            val lines = document.select(".listEvent").select("tr")
            for (line in lines) {
                var dataLine = line.select("td")
                var completeDateString = dataLine[0].text().toUpperCase()
                var completeStatusString = dataLine[1].html()
                val splitDate = completeDateString.split(" ")
                var date = splitDate[0]
                var time = splitDate[1]
                var locale = localeRegex.find(completeDateString)?.value.toString().trim().replace(" /", ",")
                var splitedLocale = locale.split(",")
                if (splitedLocale.size > 1 && splitedLocale[1].isEmpty())
                    locale = splitedLocale[0]
                val splittedStatus = completeStatusString.split("<br>")
                var observation = ""
                var status =
                    splittedStatus[0].replace("<strong>", "").replace("</strong>", "").trim()
                if (splittedStatus.size > 1 && splittedStatus[1].isNotEmpty()) {
                    observation = splittedStatus[1].trim()
                }

                tracks.add(CorreiosTrack(locale, status, observation, "$date $time", date, time, ""))
            }

            val lastTrack = tracks.first()
            val firstTrack = tracks.last()
            val isDelivered = lastTrack.status.contains("Objeto entregue")
            val typeCode = "${productCode[0]}${productCode[1]}"
            var type = CorreiosUtils.Types[typeCode]?.toUpperCase()
            if (type == null)
                type = UNKNOWN_TYPE

            return CorreiosItem(productCode, type, tracks, isDelivered, firstTrack.trackedAt, lastTrack.trackedAt)
        }*/

        suspend fun getTrackFromSite(code: String): CorreiosItem {
            val productCode = code.toUpperCase()

            if (!isValidCode(productCode)) throw IOException("Invalid Code: $code")

            val formBody = FormBody.Builder()
                .add("acao", "track")
                .add("objetos", productCode)
                .add("btnPesq", "Buscar")
                .build()

            val request = Request.Builder()
                .url(BASE_URL_SITE)
                .post(formBody)
                .build()

            val response = client.newCall(request).executeSuspend()
            if (!response.isSuccessful) throw IOException("Response: ${response.code} for code: $code")

            val tracks = mutableListOf<CorreiosTrack>()
            val document = Jsoup.parse(response.body!!.string())
            document.charset(Charset.forName("utf-8"))
            val html = document.html()
            if (html.contains(ERROR_MESSAGE) || html.contains(STATUS_WAITING)) {
                return handleWithNotPosted(productCode)
            }

            val lines = document.select(".listEvent").select("tr")
            for (line in lines) {
                var dataLine = line.select("td")
                var completeDateString = dataLine[0].text().toUpperCase()
                var completeStatusString = dataLine[1].html()
                val splitDate = completeDateString.split(" ")
                var date = splitDate[0]
                var time = splitDate[1]
                var locale = localeRegex.find(completeDateString)?.value.toString().trim().replace(" /", ",")
                var splitedLocale = locale.split(",")
                if (splitedLocale.size > 1 && splitedLocale[1].isEmpty())
                    locale = splitedLocale[0]
                val splittedStatus = completeStatusString.split("<br>")
                var observation = ""
                var status = splittedStatus[0].replace("<strong>", "").replace("</strong>", "").trim()
                var link = ""
                if (splittedStatus.size > 1) {
                    if (splittedStatus[1].contains("<!--") && splittedStatus[1].contains("TRUE")) {
                        observation = dataLine[1].text().substringBefore(".") + "."
                        if (observation.contains(status))
                            observation = observation.substringAfter(status).trim()
                        val data = dataLine[1].selectFirst("a")
                        link = data.attr("href")
                    }
                    else if (!splittedStatus[1].contains("<!--") && splittedStatus[1].isNotEmpty())
                        observation = splittedStatus[1].trim()
                }

                tracks.add(CorreiosTrack(locale, status, observation, "$date $time", date, time, link))
            }

            if (tracks.isEmpty()) throw IOException("Tracks Empty for code: $code")

            // Yes kids, last track will be the first of the list and first track will be the last of list
            val lastTrack = tracks.first() // last update
            val firstTrack = tracks.last() // posted
            val isDelivered = lastTrack.status.contains("Objeto entregue")
            val typeCode = "${productCode[0]}${productCode[1]}"
            var type = CorreiosUtils.Types[typeCode]?.toUpperCase()
            if (type == null)
                type = UNKNOWN_TYPE

            return CorreiosItem(productCode, type, tracks, isDelivered, firstTrack.trackedAt, lastTrack.trackedAt)
        }

        fun isValidCode(code: String): Boolean {
            return codeValidation.matches(code.toUpperCase())
        }

        private fun handleWithNotPosted(code: String): CorreiosItem {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")
            val dateTime = formatter.format(LocalDateTime.now())
            val splitDateTime = dateTime.split(" ")
            val date = splitDateTime[0]
            val time = splitDateTime[1]
            val tracks = listOf(CorreiosTrack(UNKNOWN_LOCATION, STATUS_WAITING, "", dateTime, date, time, ""))
            return CorreiosItem(code, UNKNOWN_TYPE, tracks, false, dateTime, dateTime, true)
        }
    }

    data class CorreiosItem(
        val code: String,
        val type: String,
        val tracks: List<CorreiosTrack>,
        val isDelivered: Boolean,
        val postedAt: String,
        val updatedAt: String,
        val isWaitingPost: Boolean = false,
    )

    data class CorreiosTrack(
        val locale: String,
        val status: String,
        val observation: String,
        val trackedAt: String,
        val date: String,
        val time: String,
        val link: String
    )
}
