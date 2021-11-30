package dev.keader.correiostracker.view.home

import android.content.Context
import android.os.Build
import androidx.lifecycle.*
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.combineWith
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TrackingRepository,
    private val preferences: PreferencesManager,
    @ApplicationContext context: Context) : ViewModel() {

    val tracks = repository.getAllItemsWithTracks()

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    private val _eventSwipeRefreshRunning = MutableLiveData(false)

    private val _isRefreshWorkRunning = Transformations.map(RefreshTracksWorker.getWorkInfoLiveData(context)) {
        if (it.isEmpty())
            return@map false
        return@map it.first().state == WorkInfo.State.RUNNING
    }

    val eventRefreshRunning = _isRefreshWorkRunning.combineWith(_eventSwipeRefreshRunning) { a, b ->
        return@combineWith a == true || b == true
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

    fun shouldShowDontKillAlert(): Boolean {
        if (!preferences.shouldShowDontKillAlert())
            return false

        val manufacturer = Build.MANUFACTURER.lowercase()
        if (manufacturer == "samsung" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            return true

        return manufacturer in badPhones
    }

    fun saveDontKillAlert() {
        viewModelScope.launch {
            preferences.saveDontKillAlert(false)
        }
    }

    private companion object {
        val badPhones = listOf(
            "oneplus",
            "huawei",
            "xiaomi",
            "meizu",
            "asus",
            "wiko",
            "lenovo",
            "oppo",
            "vivo",
            "realme",
            "blackview"
        )
    }
}
