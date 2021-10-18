package dev.keader.correiostracker.view.addpacket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPacketViewModel @Inject constructor(
    private val repository: TrackingRepository) : ViewModel() {

    private val _eventBackButtonNavigation = MutableLiveData<Event<Unit>>()
    val eventBackButtonNavigation: LiveData<Event<Unit>>
        get() = _eventBackButtonNavigation

    private val _eventAddTrack = MutableLiveData<String>()
    val eventAddTrack: LiveData<String>
        get() = _eventAddTrack

    private val _eventCheckInputs = MutableLiveData<Event<Unit>>()
    val eventCheckInputs: LiveData<Event<Unit>>
        get() = _eventCheckInputs

    val code = MutableLiveData("")
    val codeIsValid = Transformations.map(code) {
        if (it.isBlank())
            return@map true
        return@map Correios.validateCode(it)
    }

    val name = MutableLiveData("")
    val nameIsValid = Transformations.map(code) { it.isNotBlank() }

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

    fun handleAutoMove(code: String) {
        viewModelScope.launch {
            repository.archiveTrack(code)
        }
    }

    private fun getTrackInfo(code: String, observation: String) {
        viewModelScope.launch {
            _eventAddTrack.value = repository.getTrackInfoFromAPI(code, observation)
        }
    }

    fun handleBackButton() {
        _eventBackButtonNavigation.value = Event(Unit)
    }
}
