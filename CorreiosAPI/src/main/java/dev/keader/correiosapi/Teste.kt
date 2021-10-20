package dev.keader.correiosapi

import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

suspend fun main() {
    val value = Correios.getProduct("LB298117956HK")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
    exitProcess(0)
}
