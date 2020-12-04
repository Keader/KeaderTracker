package dev.keader.correiostracker.view.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.repository.TrackingRepository

class HomeViewModel @ViewModelInject constructor (repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<String>()
    val eventNavigateToTrackDetail: LiveData<String>
        get() = _eventNavigateToTrackDetail

    private val _eventOpenInfoDialog = MutableLiveData(false)
    val eventOpenInfoDialog: LiveData<Boolean>
        get() = _eventOpenInfoDialog

    fun onInfoButtonClicked() {
        _eventOpenInfoDialog.value = true
    }

    fun onInfoButtonEventFinished() {
        _eventOpenInfoDialog.value = false
    }

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = code
    }

    fun handleNavigateToTrackDetailFinish() {
        _eventNavigateToTrackDetail.value = null
    }

}
