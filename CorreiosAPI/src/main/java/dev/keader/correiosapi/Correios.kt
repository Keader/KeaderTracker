package dev.keader.correiosapi

import dev.keader.correiosapi.data.CorreiosEvento
import dev.keader.correiosapi.data.CorreiosItem
import dev.keader.correiosapi.data.CorreiosUnidade
import dev.keader.correiosapi.data.ObjetosCorreio
import dev.keader.sharedapiobjects.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://proxyapp.correios.com.br/v1/sro-rastro/"
const val CODE_VALIDATION_REGEX = "[A-Za-z]{2}[0-9]{9}[A-Za-z]{2}"
const val UNKNOWN_LOCATION = "LIMBO, DESCONHECIDO"
const val UNKNOWN_TYPE = "Desconhecido"
const val STATUS_WAITING = "Aguardando postagem pelo remetente"
const val WAITING_PAYMENT = "Aguardando pagamento"
const val ACTION_NEEDED = "Faltam informações. Sua ação é necessária"
const val MY_IMPORTS_URL = "https://cas.correios.com.br/login?service=https%3A%2F%2Fapps.correios.com.br%2Fportalimportador%2Fpages%2FpesquisarRemessaImportador%2FpesquisarRemessaImportador.jsf"
const val DELIVERY_CODE = "BDE"
const val COUNTRY = "País"
const val ERRO = "erro"

object Correios : DeliveryService {
    override val deliveryCompany = DeliveryCompany.CORREIOS
    private val codeValidation = Regex(CODE_VALIDATION_REGEX)
    private val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .followRedirects(true)
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .cookieJar(MemoryCookieJar())
        .build()

    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val foreCastFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override suspend fun getProduct(code: String): ItemWithTracks {
        val productCode = code.uppercase()
        if (!validateCode(productCode)) throw IOException("Código $code está inválido.")

        val request = Request.Builder()
            .url(BASE_URL + productCode)
            .header("Connection", "close")
            .build()

        val response = client.newCall(request).executeSuspend()
        if (!response.isSuccessful) {
            response.close()
            throw IOException("Servidor dos correios retornou erro: ${response.code} para o código: $code. Tente novamente mais tarde.")
        }

        val json = response.body!!.string()
        if (json.contains(ERRO))
            return handleWithNotPosted(productCode)

        val objetosCorreio: ObjetosCorreio
        val correiosItem: CorreiosItem
        try {
            objetosCorreio = fromJson(json)
            correiosItem = objetosCorreio.objetos.first()
        }catch (e: Exception) {
            Timber.e(e, json)
            throw IOException("Erro ao obter dados dos correios. Tente novamente mais tarde(1).")
        }

        // New api returns: "mensagem": "SRO-020: Objeto não encontrado na base de dados dos Correios."
        if (correiosItem.mensagem != null)
            return handleWithNotPosted(productCode)

        val tracks = mutableListOf<Track>()
        correiosItem.eventos.forEach { event ->
            val localDateTime: LocalDateTime
            val localDateTimeString: String
            try {
                localDateTime = LocalDateTime.parse(event.dtHrCriado)
                localDateTimeString = localDateTime.format(dateFormatter)
            } catch (e: Exception) {
                Timber.e(e)
                throw IOException("Erro ao obter dados dos correios. Tente novamente mais tarde(2).")
            }
            val dateTime = localDateTimeString.split(" ")
            val locale = handleEventAddress(event.unidade)
            val observation = handleObservation(event)
            val link = handleLink(event)
            val track = Track(
                trackUid = 0,
                itemCode = correiosItem.codObjeto,
                locale = locale,
                status = event.descricao,
                observation = observation,
                trackedAt = localDateTimeString,
                date = dateTime[0],
                time = dateTime[1],
                link = link
            )
            tracks.add(track)
        }

        var foreCast = ""
        try {
            if (correiosItem.dtPrevista != null && correiosItem.dtPrevista.isNotEmpty()) {
                val localDateTime = LocalDateTime.parse(correiosItem.dtPrevista)
                foreCast = localDateTime.format(foreCastFormatter)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        val updateDate = tracks.first().date
        val updateTime = tracks.first().time
        val postDate = tracks.last().date
        val postTime = tracks.last().time
        val item = Item(
            code = productCode,
            name = "",
            type = correiosItem.tipoPostal.descricao,
            isDelivered = correiosItem.eventos.first().descricao.contains("entregue"),
            postedAt = "$postDate $postTime",
            updatedAt = "$updateDate $updateTime",
            isArchived = false,
            isWaitingPost = false,
            deliveryCompany = deliveryCompany,
            deliveryForecast = foreCast
        )

        return ItemWithTracks(item, tracks)
    }

    private fun handleLink(event: CorreiosEvento): String {
        if (event.descricao == WAITING_PAYMENT || event.descricao == ACTION_NEEDED)
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
            return correiosUnit.nome ?: ""

        val address = correiosUnit.endereco
        var locale = ""
        address?.cidade?.let { locale += it.toCapitalize() }
        address?.uf?.let { locale += "/$it" }
        address?.siglaPais?.let { locale += " ($it)" }
        return locale
    }

    private fun handleEventAddress(correiosUnit: CorreiosUnidade): String {
        var locale = correiosUnit.nome ?: ""
        val address = correiosUnit.endereco
        address?.cidade?.let { locale += if (locale.isNotEmpty()) (", $it") else it }
        address?.uf?.let { locale += if (locale.isBlank()) it else " - $it" }
        address?.siglaPais?.let { locale += " ($it)" }
        return locale
    }

    override fun validateCode(code: String): Boolean {
        return codeValidation.matches(code.uppercase())
    }

    override fun codeHasMultipleParams() = false

    private fun handleWithNotPosted(code: String): ItemWithTracks {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
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
