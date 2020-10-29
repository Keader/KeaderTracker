package dev.keader.correiosapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder

suspend fun main() {
    val value = Correios.getTrack("PZ909656782BR")
    println(GsonBuilder().setPrettyPrinting().create().toJson(value))
}