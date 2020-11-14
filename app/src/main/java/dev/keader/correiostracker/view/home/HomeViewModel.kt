package dev.keader.correiostracker.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO

class HomeViewModel(private val database: ItemDatabaseDAO) : ViewModel() {

    val tracks = database.getAllItemWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<ItemWithTracks>()
    val eventNavigateToTrackDetail: LiveData<ItemWithTracks>
        get() = _eventNavigateToTrackDetail

    fun handleNavigateToTrackDetailFinish() {
        _eventNavigateToTrackDetail.value = null
    }

}