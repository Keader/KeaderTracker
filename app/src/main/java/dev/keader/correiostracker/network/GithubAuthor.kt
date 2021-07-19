package dev.keader.correiostracker.network

import com.google.gson.annotations.SerializedName

data class GithubAuthor(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val link: String,
    val contributions: Int
)
