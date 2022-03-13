package dev.keader.correiostracker.view.archived

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.interfaces.TrackItemListener
import javax.inject.Inject

@HiltViewModel
class ArchivedViewModel @Inject constructor(repository: TrackingRepository)
    : ViewModel(), TrackItemListener {

    val tracks = repository.getAllArchivedItemsWithTracks()
    val showEmptyTrack = tracks.map { it.isEmpty() }

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    override fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }
}
