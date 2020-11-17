package dev.keader.correiostracker.view.trackdetail

import android.view.View
import androidx.lifecycle.*
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackDetailViewModel(
    private val database: ItemDatabaseDAO,
    val trackCode: String) : ViewModel() {

    val trackItem = database.getTrackingWithTracks(trackCode)

    val isArchived = Transformations.map(trackItem) {
        it.item.isArchived
    }

    private val _eventFloatButton = MutableLiveData(false)
    val eventFloatButton: LiveData<Boolean>
        get() = _eventFloatButton

    fun onFloatButtonPressed(view: View) {
        when(view.getTag(R.id.tag_archived)) {
            TAG_VALUE_UNARCHIVED -> handleArchiveTrack()
            TAG_VALUE_ARCHIVED -> handleUnArchiveTrack()
        }
    }

    private fun handleArchiveTrack() {
        viewModelScope.launch {
            archiveTrack()
            _eventFloatButton.value = true
        }
    }

    private suspend fun archiveTrack() {
        return withContext(Dispatchers.IO) {
            database.archiveTrack(trackCode)
        }
    }

    private fun handleUnArchiveTrack() {
        viewModelScope.launch {
            unArchiveTrack()
            _eventFloatButton.value = true
        }
    }

    private suspend fun unArchiveTrack() {
        return withContext(Dispatchers.IO) {
            database.unArchiveTrack(trackCode)
        }
    }

    fun onFloatButtonComplete() {
        _eventFloatButton.value = false
    }
}