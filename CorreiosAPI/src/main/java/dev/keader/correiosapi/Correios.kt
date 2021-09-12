package dev.keader.correiosapi

import dev.keader.sharedapiobjects.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.jsoup.Jsoup
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://www2.correios.com.br/sistemas/rastreamento/resultado_semcontent.cfm"
const val BASE_URL_SITE = "https://www2.correios.com.br/sistemas/rastreamento/ctrl/ctrlRastreamento.cfm?"
const val LOCALE_REGEX = "[[A-Z]+\\s*]+/[\\s*[A-Z]+]*"
const val CODE_VALIDATION_REGEX = "[A-Za-z]{2}[0-9]{9}[A-Za-z]{2}"
const val ERROR_MESSAGE = "Não é possível exibir informações para o código informado."
const val UNKNOWN_LOCATION = "LIMBO, DESCONHECIDO"
const val UNKNOWN_TYPE = "Desconhecido - "
const val STATUS_WAITING = "Aguardando postagem pelo remetente."

object Correios : DeliveryService {
    private val deliveryCompany = DeliveryCompany.CORREIOS
    private val localeRegex = Regex(LOCALE_REGEX)
    private val codeValidation = Regex(CODE_VALIDATION_REGEX)
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .cookieJar(MemoryCookieJar())
        .build()

    override suspend fun getProduct(code: String): ItemWithTracks {
        val productCode = code.uppercase()

        if (!validateCode(productCode)) throw IOException("Invalid Code: $code")

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
        if (!response.isSuccessful) {
            response.close()
            throw IOException("Response: ${response.code} for code: $code")
        }

        val tracks = mutableListOf<Track>()
        val document = Jsoup.parse(response.body!!.string())
        document.charset(Charset.forName("utf-8"))
        val html = document.html()
        if (html.contains(ERROR_MESSAGE) || html.contains(STATUS_WAITING)) {
            return handleWithNotPosted(productCode)
        }

        val lines = document.select(".listEvent").select("tr")
        for (line in lines) {
            val dataLine = line.select("td")
            val completeDateString = dataLine[0].text().uppercase()
            val completeStatusString = dataLine[1].html()
            val splitDate = completeDateString.split(" ")
            val date = splitDate[0]
            val time = splitDate[1]
            var locale = localeRegex.find(completeDateString)?.value.toString().trim().replace(" /", ",")
            val splitedLocale = locale.split(",")
            if (splitedLocale.size > 1 && splitedLocale[1].isEmpty())
                locale = splitedLocale[0]
            val splittedStatus = completeStatusString.split("<br>")
            var observation = ""
            val status = splittedStatus[0].replace("<strong>", "").replace("</strong>", "").trim()
            var link = ""
            if (splittedStatus.size > 1) {
                if (splittedStatus[1].contains("<!--") && splittedStatus[1].contains("TRUE")) {
                    observation = dataLine[1].text().substringBefore(".") + "."
                    if (observation.contains(status))
                        observation = observation.substringAfter(status).trim()
                    val data = dataLine[1].selectFirst("a")
                    link = data?.attr("href")?.trim() ?: ""
                }
                else if (splittedStatus[1].contains("href")) {
                    val data = dataLine[1].selectFirst("a")
                    link = data?.attr("href")?.trim() ?: ""
                }
                else if (!splittedStatus[1].contains("<!--") && splittedStatus[1].isNotEmpty())
                    observation = splittedStatus[1].trim()
            }

            tracks.add(Track(
                trackUid = 0, itemCode = productCode, locale = locale, status = status,
                observation = observation, trackedAt = "$date $time", date = date, time = time,
                link = link))
        }

        if (tracks.isEmpty()) throw IOException("Tracks Empty for code: $code")

        // Yes kids, last track will be the first of the list and first track will be the last of list
        val lastTrack = tracks.first() // last update
        val firstTrack = tracks.last() // posted
        val isDelivered = lastTrack.status.contains("Objeto entregue")
        val typeCode = "${productCode[0]}${productCode[1]}"
        var type = CorreiosUtils.Types[typeCode]
        if (type == null)
            type = "$UNKNOWN_TYPE $typeCode"

        val item = Item(
            code = productCode, name = "", type = type, isDelivered = isDelivered,
            postedAt = firstTrack.trackedAt, updatedAt = lastTrack.trackedAt,
            isArchived = false, isWaitingPost = false, deliveryCompany = deliveryCompany)
        return ItemWithTracks(item, tracks)
    }

    override fun validateCode(code: String): Boolean {
        return codeValidation.matches(code.uppercase())
    }

    override fun codeHasMultipleParams() = false

    override fun getDeliveryCompany() = deliveryCompany

    private fun handleWithNotPosted(code: String): ItemWithTracks {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")
        val dateTime = formatter.format(LocalDateTime.now())
        val splitDateTime = dateTime.split(" ")
        val date = splitDateTime[0]
        val time = splitDateTime[1]
        val tracks = listOf(Track(0, code, UNKNOWN_LOCATION, STATUS_WAITING, "", dateTime, date, time, ""))
        val item = Item(
            code = code, name = "", type = UNKNOWN_TYPE, isDelivered = false,
            postedAt = dateTime, updatedAt = dateTime, isArchived = false,
            isWaitingPost = true, deliveryCompany = deliveryCompany
        )
        return ItemWithTracks(item, tracks)
    }
}
