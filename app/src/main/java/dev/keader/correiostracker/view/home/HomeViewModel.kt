package dev.keader.correiostracker.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllItemsWithTracks()

    init{
        viewModelScope.launch {
            repository.refreshTracks()
        }
    }

    private val _eventNavigateToTrackDetail = MutableLiveData<String>()
    val eventNavigateToTrackDetail: LiveData<String>
        get() = _eventNavigateToTrackDetail

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = code
    }

    fun handleNavigateToTrackDetailFinish() {
        _eventNavigateToTrackDetail.value = null
    }

}
