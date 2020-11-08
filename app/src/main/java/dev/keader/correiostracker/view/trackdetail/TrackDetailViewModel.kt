package dev.keader.correiostracker.view.trackdetail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO

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
        _eventFloatButton.value = true
    }

    private fun handleArchiveTrack() {
        // Action in progress already
        if (_eventFloatButton.value!!)
            return

        // TODO: IMPLEMENT ME !
        onFloatButtonComplete()
    }

    private fun handleUnArchiveTrack() {
        // TODO: IMPLEMENT ME !
        onFloatButtonComplete()
    }

    fun onFloatButtonComplete() {
        _eventFloatButton.value = false
    }
}