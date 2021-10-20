package dev.keader.correiosapi.data

data class CorreiosItem(
    val codObjeto: String,
    val tipoPostal: PostalType,
    val dtPrevista: String?,
    val eventos: List<CorreiosEvento>,
    val situacao: String?,
    val mensagem: String?
)

data class ObjetosCorreio(
    val objetos: List<CorreiosItem>
)
