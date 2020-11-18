package dev.keader.correiostracker.view.addpacket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.repository.TrackingRepository

class AddPacketViewModelFactory(private val repository: TrackingRepository) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPacketViewModel::class.java)) {
            return AddPacketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
