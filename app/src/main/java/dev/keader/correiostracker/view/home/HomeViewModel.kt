package dev.keader.correiostracker.view.home

import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.model.combineWith
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TrackingRepository,
    @ApplicationContext context: Context) : ViewModel() {

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

    private val _eventSwipeRefreshRunning = MutableLiveData(false)

    private val _isRefreshWorkRunning = Transformations.map(RefreshTracksWorker.getWorkInfoLiveData(context)) {
        if (it.isEmpty())
            return@map false
        return@map it.first().state == WorkInfo.State.RUNNING
    }

    val eventRefreshRunning = _isRefreshWorkRunning.combineWith(_eventSwipeRefreshRunning) { a, b ->
        return@combineWith a == true || b == true
    }

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
        _eventSwipeRefreshRunning.value = true
        viewModelScope.launch {
            repository.refreshTracks()
            _eventSwipeRefreshRunning.value = false
        }
    }
}
