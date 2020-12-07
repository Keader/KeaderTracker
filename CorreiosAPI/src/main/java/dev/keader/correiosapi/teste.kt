package dev.keader.correiosapi

import com.google.gson.GsonBuilder
import kotlin.system.exitProcess

suspend fun main() {
    val value = Correios.getTrackFromSite(/*"LB915447474SE"*/"LB949929714SE")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
    //println(value)
    exitProcess(0)
}
