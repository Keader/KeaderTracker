package dev.keader.correiosapi

import dev.keader.correiosapi.data.CorreiosEvento
import dev.keader.correiosapi.data.CorreiosItem
import dev.keader.correiosapi.data.CorreiosUnidade
import dev.keader.sharedapiobjects.DeliveryCompany
import dev.keader.sharedapiobjects.DeliveryService
import dev.keader.sharedapiobjects.Item
import dev.keader.sharedapiobjects.ItemWithTracks
import dev.keader.sharedapiobjects.Track
import dev.keader.sharedapiobjects.fromJson
import dev.keader.sharedapiobjects.toCapitalize
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://rastreamento.correios.com.br/app/resultado.php?objeto="
const val CODE_VALIDATION_REGEX = "[A-Za-z]{2}[0-9]{9}[A-Za-z]{2}"
const val UNKNOWN_LOCATION = "LIMBO, DESCONHECIDO"
const val UNKNOWN_TYPE = "Desconhecido"
const val STATUS_WAITING = "Aguardando postagem pelo remetente"
const val WAITING_PAYMENT = "Aguardando pagamento"
const val MY_IMPORTS_URL = "https://cas.correios.com.br/login?service=https%3A%2F%2Fapps.correios.com.br%2Fportalimportador%2Fpages%2FpesquisarRemessaImportador%2FpesquisarRemessaImportador.jsf"
const val DELIVERY_STATUS = "E"
const val COUNTRY = "País"
const val ERRO = "erro"

object Correios : DeliveryService {
    override val deliveryCompany = DeliveryCompany.CORREIOS
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

        val request = Request.Builder()
            .url(BASE_URL + productCode)
            .build()

        var response: Response
        try {
            response = client.newCall(request).executeSuspend()
            if (!response.isSuccessful) {
                response.close()
                throw IOException("Response: ${response.code} for code: $code")
            }
        } catch (ex: Exception) {
            return handleWithNotPosted(productCode)
        }

        val json = response.body!!.string()
        if (json.contains(ERRO))
            return handleWithNotPosted(productCode)

        val correiosItem = fromJson<CorreiosItem>(json)

        val tracks = mutableListOf<Track>()
        correiosItem.eventos.forEach { event ->
            val dateTime = event.dtHrCriado.split(" ")
            val locale = handleEventAddress(event.unidade)
            val observation = handleObservation(event)
            val link = handleLink(event)
            val track = Track(
                trackUid = 0,
                itemCode = correiosItem.codObjeto,
                locale = locale,
                status = event.descricao,
                observation = observation,
                trackedAt = event.dtHrCriado,
                date = dateTime[0],
                time = dateTime[1],
                link = link
            )
            tracks.add(track)
        }

        val updateDate = tracks.first().date
        val updateTime = tracks.first().time
        val postDate = tracks.last().date
        val postTime = tracks.last().time
        val item = Item(
            code = productCode,
            name = "",
            type = correiosItem.tipoPostal.descricao,
            isDelivered = correiosItem.situacao == DELIVERY_STATUS,
            postedAt = "$postDate $postTime",
            updatedAt = "$updateDate $updateTime",
            isArchived = false,
            isWaitingPost = false,
            deliveryCompany = deliveryCompany,
            deliveryForecast = correiosItem.dtPrevista
        )

        return ItemWithTracks(item, tracks)
    }

    private fun handleLink(event: CorreiosEvento): String {
        if (event.descricao == WAITING_PAYMENT)
            return MY_IMPORTS_URL
        return ""
    }

    private fun handleObservation(event: CorreiosEvento): String {
        if (event.unidadeDestino == null)
            return ""

        val localeOrigin = handleObservationAddress(event.unidade)
        val localeDestiny = handleObservationAddress(event.unidadeDestino)
        return "de ${event.unidade.tipo} em $localeOrigin para a ${event.unidadeDestino.tipo} em $localeDestiny."
    }

    private fun handleObservationAddress(correiosUnit: CorreiosUnidade): String {
        if (correiosUnit.tipo == COUNTRY)
            return correiosUnit.nome

        val address = correiosUnit.endereco
        var locale = ""
        address.cidade?.let { locale += it.toCapitalize() }
        address.uf?.let { locale += "/$it" }
        address.siglaPais?.let { locale += " ($it)" }
        return locale
    }

    private fun handleEventAddress(correiosUnit: CorreiosUnidade): String {
        var locale = correiosUnit.nome
        val address = correiosUnit.endereco
        address.cidade?.let {
            locale += if (locale.isNotEmpty())
                (", $it")
            else
                it
        }
        address.uf?.let { locale += " - $it" }
        address.siglaPais?.let { locale += " ($it)" }
        return locale
    }

    override fun validateCode(code: String): Boolean {
        return codeValidation.matches(code.uppercase())
    }

    override fun codeHasMultipleParams() = false

    private fun handleWithNotPosted(code: String): ItemWithTracks {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val dateTime = formatter.format(LocalDateTime.now())
        val splitDateTime = dateTime.split(" ")
        val date = splitDateTime[0]
        val time = splitDateTime[1]
        val tracks = listOf(Track(0, code, UNKNOWN_LOCATION, STATUS_WAITING, "", dateTime, date, time, ""))
        val item = Item(
            code = code, name = "", type = UNKNOWN_TYPE, isDelivered = false,
            postedAt = dateTime, updatedAt = dateTime, isArchived = false,
            isWaitingPost = true, deliveryCompany = deliveryCompany, deliveryForecast = ""
        )
        return ItemWithTracks(item, tracks)
    }
}
