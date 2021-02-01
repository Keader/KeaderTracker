package dev.keader.correiostracker.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.util.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TrackingRepository) : ViewModel() {

    val tracks = repository.getAllItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    private val _eventOpenInfoDialog = MutableLiveData<Event<Unit>>()
    val eventOpenInfoDialog: LiveData<Event<Unit>>
        get() = _eventOpenInfoDialog

    private val _eventOpenSettingsFragment = MutableLiveData<Event<Unit>>()
    val eventOpenSettingsFragment: LiveData<Event<Unit>>
        get() = _eventOpenSettingsFragment


    private val _eventRefreshFinished = MutableLiveData(true)
    val eventRefreshFinished: LiveData<Boolean>
        get() = _eventRefreshFinished

    fun onInfoButtonClicked() {
        _eventOpenInfoDialog.value = Event(Unit)
    }

    fun onSettingsButtonClicked() {
        _eventOpenSettingsFragment.value = Event(Unit)
    }

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }

    fun onRefreshCalled() {
        _eventRefreshFinished.value = false
        viewModelScope.launch {
            repository.refreshTracks()
            _eventRefreshFinished.value = true
        }
    }

}
