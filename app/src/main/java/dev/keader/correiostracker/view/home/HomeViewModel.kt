package dev.keader.correiostracker.view.home

import android.os.Build
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.combineWith
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.interfaces.TrackItemListener
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TrackingRepository,
    private val preferences: PreferencesManager,
) : ViewModel(), TrackItemListener {

    val tracks = repository.getAllItemsWithTracks()
    val showEmptyTrack = tracks.map { it.isEmpty() }

    private val _eventNavigateToTrackDetail = MutableLiveData<Event<String>>()
    val eventNavigateToTrackDetail: LiveData<Event<String>>
        get() = _eventNavigateToTrackDetail

    private val _eventShowDontKillAlert = MutableLiveData<Event<Unit>>()
    val eventShowDontKillAlert: LiveData<Event<Unit>>
        get() = _eventShowDontKillAlert

    private val _eventSwipeRefreshRunning = MutableLiveData(false)

    private val _isRefreshWorkRunning = repository.getRefreshWorkInfo()

    val eventRefreshRunning = _isRefreshWorkRunning.combineWith(_eventSwipeRefreshRunning) { a, b ->
        return@combineWith a == true || b == true
    }

    override fun onItemTrackClicked(code: String) {
        _eventNavigateToTrackDetail.value = Event(code)
    }

    fun onRefreshCalled() {
        _eventSwipeRefreshRunning.value = true
        viewModelScope.launch {
            repository.refreshTracks()
            _eventSwipeRefreshRunning.value = false
        }
    }

    fun checkDontKillAlert() {
        if (shouldShowDontKillAlert()) {
            _eventShowDontKillAlert.value = Event(Unit)
            saveDontKillAlert()
        }
    }

    private fun shouldShowDontKillAlert(): Boolean {
        if (!preferences.shouldShowDontKillAlert())
            return false

        val manufacturer = Build.MANUFACTURER.lowercase()
        if (manufacturer == "samsung" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            return true

        return manufacturer in badPhones
    }

    private fun saveDontKillAlert() {
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
