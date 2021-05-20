package dev.keader.correiostracker.view.home

import android.content.Context
import androidx.lifecycle.*
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.util.Event
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: TrackingRepository,
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

    private val _eventRefreshRunning = Transformations.map(RefreshTracksWorker.getWorkInfoLiveData(context)) {
        if (it.isEmpty())
            return@map false
        return@map it.first().state == WorkInfo.State.RUNNING
    }
    val eventRefreshRunning: LiveData<Boolean>
        get() = _eventRefreshRunning

    fun onInfoButtonClicked() {
        _eventOpenInfoDialog.value = Event(Unit)
    }

    fun onSettingsButtonClicked() {
        _eventOpenSettingsFragment.value = Event(Unit)
    }

    fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }

    fun onRefreshCalled(context: Context) {
        RefreshTracksWorker.startWorker(context)
    }
}
