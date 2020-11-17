package dev.keader.correiostracker.view.archived

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO

class ArchivedViewModel(private val database: ItemDatabaseDAO) : ViewModel() {

    val tracks = database.getAllArchivedItemsWithTracks()

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