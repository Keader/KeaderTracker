package dev.keader.correiosapi.data

data class CorreiosEvento(
    val dtHrCriado: String,
    val descricao: String,
    val unidade: CorreiosUnidade,
    val unidadeDestino: CorreiosUnidade?,
    val descricaoFrontEnd: String,
    val finalizador: String,
)
