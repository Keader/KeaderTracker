package dev.keader.correiosapi

import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

suspend fun main() {
    val value = Correios.getProduct("LE350041638SE")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
    exitProcess(0)
}
