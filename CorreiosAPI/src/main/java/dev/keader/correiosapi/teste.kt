package dev.keader.correiosapi

import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

suspend fun main() {
    val value = Correios.getProduct("NX134399050BR")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
    //println(value)
    exitProcess(0)
}
