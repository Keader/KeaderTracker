package dev.keader.correiostracker.view.addpacket

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch

class AddPacketViewModel @ViewModelInject constructor(
    private val repository: TrackingRepository) : ViewModel() {

    private val _eventCancelButtonNavigation = MutableLiveData<Boolean>()
    val eventCancelButtonNavigation: LiveData<Boolean>
        get() = _eventCancelButtonNavigation

    private val _eventAddTrack = MutableLiveData<Boolean?>()
    val eventAddTrack: LiveData<Boolean?>
        get() = _eventAddTrack

    private val _eventCheckInputs = MutableLiveData<Boolean>()
    val eventCheckInputs: LiveData<Boolean>
        get() = _eventCheckInputs

    private val _eventQR = MutableLiveData<Boolean>()
    val eventQR: LiveData<Boolean>
        get() = _eventQR

    init {
        _eventCancelButtonNavigation.value = false
        _eventCheckInputs.value = false
        _eventQR.value = false
    }

    fun handleQRButton() {
        _eventQR.value = true
    }

    fun qRCodeEventFinished() {
        _eventQR.value = false
    }

    fun handleOKButton() {
        _eventCheckInputs.value = true
    }

    fun handleCheckOK(code: String, observation: String) {
        _eventCheckInputs.value = false
        getTrackInfo(code, observation)
    }

    fun handleAutoSave(code: String) {
        viewModelScope.launch {
            repository.archiveTrack(code)
        }
    }

    private fun getTrackInfo(code: String, observation: String) {
        viewModelScope.launch {
            _eventAddTrack.value = repository.getTrackInfoFromAPI(code, observation)
        }
    }

    fun handleCheckFail() {
        _eventCheckInputs.value = false
    }

    fun handleCancelButton() {
        _eventCancelButtonNavigation.value = true
    }

    fun handleAddEventFinish() {
        _eventAddTrack.value = null
    }

    fun handleCancelButtonEventFinish() {
        _eventCancelButtonNavigation.value = false
    }
}
