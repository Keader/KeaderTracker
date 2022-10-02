package dev.keader.correiostracker.view.authors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.network.GithubAuthor
import dev.keader.correiostracker.repository.AuthorsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorsViewModel @Inject constructor(
    private val repository: AuthorsRepository
) : ViewModel() {

    private val _authors = MutableLiveData<List<GithubAuthor>>(emptyList())
    val authors: LiveData<List<GithubAuthor>>
        get() = _authors

    init {
        fetchGithubAuthors()
    }

    private fun fetchGithubAuthors() {
        viewModelScope.launch {
            _authors.value = repository.getAuthors()
        }
    }
}
