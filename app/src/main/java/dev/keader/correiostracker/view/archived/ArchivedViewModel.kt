package dev.keader.correiostracker.view.archived

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.model.Event
import javax.inject.Inject

@HiltViewModel
class ArchivedViewModel @Inject constructor(repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllArchivedItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }
}
