package dev.keader.correiostracker.view.archived

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.repository.TrackingRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

    @Throws(Exception::class)
    fun parseDate(dateTime: String, withSeconds: Boolean = true, withoutBar: Boolean = false): Long {
        val formatter = when {
            withSeconds -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            withoutBar -> DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            else -> DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        }
        val localDateTime = LocalDateTime.parse(dateTime, formatter)
        return localDateTime.until(LocalDateTime.now(), ChronoUnit.DAYS)
    }
}
