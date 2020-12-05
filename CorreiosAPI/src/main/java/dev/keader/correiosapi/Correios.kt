package dev.keader.correiosapi

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val BASE_URL =
    "https://www2.correios.com.br/sistemas/rastreamento/resultado_semcontent.cfm"
const val LOCALE_REGEX = "[[A-Z]+\\s*]+/[\\s*[A-Z]+]*"
const val CODE_VALIDATION_REGEX = "^[A-Z]{2}[0-9]{9}[A-Z]{2}"
const val ERROR_MESSAGE = "Não é possível exibir informações para o código informado."
const val UNKNOWN_LOCATION = "DESCONHECIDO, ZZ"
const val UNKNOWN_TYPE = "DESCONHECIDO"
const val STATUS_WAITING = "Aguardando postagem pelo remetente."

class Correios {
    companion object {
        private val localeRegex = Regex(LOCALE_REGEX)
        private val codeValidation = Regex(CODE_VALIDATION_REGEX)
        private val client = OkHttpClient()

        suspend fun getTrack(code: String): CorreiosItem {
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
            if (!response.isSuccessful) throw IOException("${response.code}")

            val tracks = mutableListOf<CorreiosTrack>()
            val document = Jsoup.parse(response.body!!.string())
            if (document.html().contains(ERROR_MESSAGE)) {
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

                tracks.add(CorreiosTrack(locale, status, observation, "$date $time", date, time))
            }

            val lastTrack = tracks.first()
            val firstTrack = tracks.last()
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

        private fun handleWithNotPosted(code: String) : CorreiosItem {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")
            val dateTime = formatter.format(LocalDateTime.now())
            val splitDateTime = dateTime.split(" ")
            val date = splitDateTime[0]
            val time = splitDateTime[1]
            val tracks = listOf(CorreiosTrack(UNKNOWN_LOCATION, STATUS_WAITING, "", dateTime, date, time))
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
            val time: String
    )
}
