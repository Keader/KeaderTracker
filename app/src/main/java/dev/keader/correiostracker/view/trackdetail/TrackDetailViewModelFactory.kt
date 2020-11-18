package dev.keader.correiostracker.view.trackdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.repository.TrackingRepository

class TrackDetailViewModelFactory(
        private val repository: TrackingRepository,
        private val code: String) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackDetailViewModel::class.java)) {
            return TrackDetailViewModel(repository, code) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
