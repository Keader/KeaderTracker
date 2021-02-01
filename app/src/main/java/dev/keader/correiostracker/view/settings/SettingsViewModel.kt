package dev.keader.correiostracker.view.settings

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
class SettingsViewModel @Inject constructor(
    private val repository: TrackingRepository): ViewModel() {

    private val _eventNavigateBack = MutableLiveData<Event<Unit>>()
    val eventNavigateBack: LiveData<Event<Unit>>
        get() = _eventNavigateBack

    private val _eventNavigateOK = MutableLiveData<Event<Unit>>()
    val eventNavigateOK: LiveData<Event<Unit>>
        get() = _eventNavigateOK

    fun handleArchiveAllCurrentItems() {
        viewModelScope.launch {
            repository.archiveAllDeliveredItems()
        }
    }

    fun onCancelButtonClicked() {
        _eventNavigateBack.value = Event(Unit)
    }

    fun onOKButtonClicked() {
        _eventNavigateOK.value = Event(Unit)
    }

}
