package dev.keader.correiosapi.data

data class CorreiosEvento(
    val codigo: String,
    val dtHrCriado: String,
    val descricao: String,
    val unidade: CorreiosUnidade,
    val unidadeDestino: CorreiosUnidade?,
    val finalizador: String?,
)
