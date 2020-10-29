package dev.keader.correiosapi

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.jsoup.Jsoup

const val BASE_URL: String =
    "https://www2.correios.com.br/sistemas/rastreamento/resultado_semcontent.cfm"
const val LOCALE_REGEX: String = "[[A-Z]+\\s*?]*?/\\s*?[A-Z]+"
const val CODE_VALIDATION_REGEX: String = "^[A-Z]{2}[0-9]{9}[A-Z]{2}"
const val ERROR_MESSAGE = "Não é possível exibir informações para o código informado."

class Correios {
    companion object {
        private val localeRegex = Regex(LOCALE_REGEX)
        private val codeValidation = Regex(CODE_VALIDATION_REGEX)
        private val client = OkHttpClient()

        suspend fun getTrack(code: String): CorreiosItem {

            if (!isValidCode(code)) throw IOException("-1")

            var item = CorreiosItem(code)

            val formBody = FormBody.Builder()
                .add("objetos", code)
                .build()

            val request = Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build()

            val response = client.newCall(request).executeSuspend()
            if (!response.isSuccessful) throw IOException("${response.code}")

            val tracks = mutableListOf<Track>()
            val document = Jsoup.parse(response.body!!.string())
            if (document.html().contains(ERROR_MESSAGE)) throw IOException("404")

            var lines = document.select(".listEvent").select("tr")
            for (line in lines) {
                var dataLine = line.select("td")
                var completeDateString = dataLine[0].text()
                var completeStatusString = dataLine[1].html()
                val splitDate = completeDateString.split(" ")
                var date = splitDate[0]
                var time = splitDate[1]
                var locale = localeRegex.find(completeDateString)?.value.toString().trim()
                val splittedStatus = completeStatusString.split("<br>")
                var observation = ""
                var status =
                    splittedStatus[0].replace("<strong>", "").replace("</strong>", "").trim()
                if (splittedStatus.size > 1 && splittedStatus[1].isNotEmpty()) {
                    observation = splittedStatus[1].trim()
                }

                var track = Track(locale, status, observation, "$date $time")
                tracks.add(track)
            }

            item.tracks = tracks
            val lastTrack = tracks.first()
            val firstTrack = tracks.last()
            item.isDelivered = lastTrack.status.contains("Objeto entregue")
            item.postedAt = firstTrack.trackedAt
            item.updatedAt = lastTrack.trackedAt
            return item
        }

        fun isValidCode(code: String): Boolean {
            return codeValidation.matches(code)
        }
    }

    data class CorreiosItem(val code: String) {
        var type: String = ""
        var tracks: List<Track>? = null
        var isDelivered: Boolean = false
        var postedAt: String = ""
        var updatedAt: String = ""
    }

    data class Track(
        val locale: String,
        val status: String,
        val observation: String,
        val trackedAt: String
    ) { }
}