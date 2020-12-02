package dev.keader.correiosapi

import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

suspend fun main() {
    val value = Correios.getTrack("OM824790058BR")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
    //println(value)
    exitProcess(0)
}
