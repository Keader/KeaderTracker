package dev.keader.correiostracker.repository

import dev.keader.correiostracker.network.GithubAuthor
import dev.keader.correiostracker.network.GithubService
import timber.log.Timber
import javax.inject.Inject

class AuthorsRepository @Inject constructor(private val githubService: GithubService) {

    suspend fun getAuthors(): List<GithubAuthor> {
        try {
            val finalList = mutableListOf<GithubAuthor>()
            finalList.addAll(githubService.getContributors())
            finalList.addAll(getLocalAuthors())
            return finalList.sortedByDescending { it.contributions }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        return emptyList()
    }

    private fun getLocalAuthors() : List<GithubAuthor> {
        val list = mutableListOf<GithubAuthor>()

        list.add(GithubAuthor(
            "MimaCobaltini",
            "https://i.imgur.com/DGWRgy0.jpeg",
            "https://linkedin.com/in/mimacobaltini",
            21
        ))

        return list
    }
}
