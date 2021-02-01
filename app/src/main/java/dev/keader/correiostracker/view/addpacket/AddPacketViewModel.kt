package dev.keader.correiostracker.view.addpacket

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
class AddPacketViewModel @Inject constructor(
    private val repository: TrackingRepository) : ViewModel() {

    private val _eventCancelButtonNavigation = MutableLiveData<Event<Unit>>()
    val eventCancelButtonNavigation: LiveData<Event<Unit>>
        get() = _eventCancelButtonNavigation

    private val _eventAddTrack = MutableLiveData<Event<Boolean>>()
    val eventAddTrack: LiveData<Event<Boolean>>
        get() = _eventAddTrack

    private val _eventCheckInputs = MutableLiveData<Event<Unit>>()
    val eventCheckInputs: LiveData<Event<Unit>>
        get() = _eventCheckInputs

    private val _eventQR = MutableLiveData<Event<Unit>>()
    val eventQR: LiveData<Event<Unit>>
        get() = _eventQR

    fun handleQRButton() {
        _eventQR.value = Event(Unit)
    }

    fun handleOKButton() {
        _eventCheckInputs.value = Event(Unit)
    }

    fun handleCheckOK(code: String, observation: String) {
        getTrackInfo(code, observation)
    }

    fun handleAutoSave(code: String) {
        viewModelScope.launch {
            repository.archiveTrack(code)
        }
    }

    private fun getTrackInfo(code: String, observation: String) {
        viewModelScope.launch {
            _eventAddTrack.value = Event(repository.getTrackInfoFromAPI(code, observation))
        }
    }

    fun handleCancelButton() {
        _eventCancelButtonNavigation.value = Event(Unit)
    }
}
