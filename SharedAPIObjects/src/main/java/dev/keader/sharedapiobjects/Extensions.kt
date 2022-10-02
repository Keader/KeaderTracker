package dev.keader.sharedapiobjects

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.util.Locale

inline fun <reified T> fromJson(src: String, gson: Gson = Gson()): T {
    val type = object : TypeToken<T>() { }.type
    return gson.fromJson(src, type)
}

inline fun <reified T> fromJson(reader: Reader, gson: Gson = Gson()): T {
    return gson.fromJson(reader, T::class.java)
}

fun String.toCapitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(Locale.getDefault())
        } else {
            it.toString()
        }
    }
}
