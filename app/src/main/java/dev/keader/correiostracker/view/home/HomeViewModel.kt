package dev.keader.correiostracker.view.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(private val repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<String>()
    val eventNavigateToTrackDetail: LiveData<String>
        get() = _eventNavigateToTrackDetail

    private val _eventOpenInfoDialog = MutableLiveData(false)
    val eventOpenInfoDialog: LiveData<Boolean>
        get() = _eventOpenInfoDialog

    private val _eventOpenSettingsFragment = MutableLiveData(false)
    val eventOpenSettingsFragment: LiveData<Boolean>
        get() = _eventOpenSettingsFragment


    private val _eventRefreshFinished = MutableLiveData(true)
    val eventRefreshFinished: LiveData<Boolean>
        get() = _eventRefreshFinished

    fun onInfoButtonClicked() {
        _eventOpenInfoDialog.value = true
    }

    fun onInfoButtonEventFinished() {
        _eventOpenInfoDialog.value = false
    }

    fun onSettingsButtonClicked() {
        _eventOpenSettingsFragment.value = true
    }

    fun onSettingsButtonEventFinished() {
        _eventOpenSettingsFragment.value = false
    }

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = code
    }

    fun handleNavigateToTrackDetailFinish() {
        _eventNavigateToTrackDetail.value = null
    }

    fun onRefreshCalled() {
        _eventRefreshFinished.value = false
        viewModelScope.launch {
            repository.refreshTracks()
            _eventRefreshFinished.value = true
        }
    }

}
