package dev.keader.correiostracker.network

import retrofit2.http.GET

interface GithubService {
    @GET("repos/keader/KeaderTracker/contributors")
    suspend fun getContributors(): List<GithubAuthor>
}
