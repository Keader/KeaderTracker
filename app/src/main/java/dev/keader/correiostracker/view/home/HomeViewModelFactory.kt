package dev.keader.correiostracker.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.repository.TrackingRepository

class HomeViewModelFactory(private val repository: TrackingRepository) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
