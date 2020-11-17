package dev.keader.correiostracker.view.trackdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO

class TrackDetailViewModelFactory(
    private val dataSource: ItemDatabaseDAO,
    private val code: String) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackDetailViewModel::class.java)) {
            return TrackDetailViewModel(dataSource, code) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}