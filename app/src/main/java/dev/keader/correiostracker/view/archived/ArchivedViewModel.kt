package dev.keader.correiostracker.view.archived

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.repository.TrackingRepository

class ArchivedViewModel @ViewModelInject constructor (private val repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllArchivedItemsWithTracks()

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
