package dev.keader.correiosapi.data

data class CorreiosItem(
    val codObjeto: String,
    val tipoPostal: PostalType,
    val dtPrevisaoEntrega: String,
    val eventos: List<CorreiosEvento>,
    val situacao: String
)
