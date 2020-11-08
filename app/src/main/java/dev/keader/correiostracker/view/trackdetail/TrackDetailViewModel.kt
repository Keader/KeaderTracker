package dev.keader.correiostracker.view.trackdetail

import android.view.View
import androidx.lifecycle.*
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackDetailViewModel(
    val database: ItemDatabaseDAO,
    val trackCode: String) : ViewModel() {

    //TODO: Handle with the list

    private val _trackItem = database.getTrackingWithTracks(trackCode)

    val trackTitle = Transformations.map(_trackItem) {
        it.item.name
    }

    val trackCodeString = Transformations.map(_trackItem) {
        "Código ${it.item.code}"
    }

    val trackStartLocale = Transformations.map(_trackItem) {
        it.tracks.last().locale
    }

    val trackPostedAt = Transformations.map(_trackItem) {
        "Postado em ${it.item.postedAt}"
    }

    val trackLastUpdate = Transformations.map(_trackItem) {
        "Ultima atualização em ${it.item.updatedAt}"
    }

    val isArchived = Transformations.map(_trackItem) {
        it.item.isArchived
    }

    private val _eventFloatButton = MutableLiveData(false)
    val eventFloatButton: LiveData<Boolean>
        get() = _eventFloatButton

    fun onFloatButtonPressed(view: View) {
        when(view.getTag(TAG_ARCHIVED)) {
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