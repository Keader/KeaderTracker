package dev.keader.correiostracker.view.archived

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.util.Event

class ArchivedViewModel @ViewModelInject constructor(repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllArchivedItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }
}
