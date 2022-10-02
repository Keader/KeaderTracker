package dev.keader.correiostracker.view.addpacket

import android.app.Activity
import android.content.ClipData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPacketViewModel @Inject constructor(
    private val repository: TrackingRepository,
    private val preferences: PreferencesManager
) : ViewModel() {

    private val _eventBackButtonNavigation = MutableLiveData<Event<Unit>>()
    val eventBackButtonNavigation: LiveData<Event<Unit>>
        get() = _eventBackButtonNavigation

    private val _eventAddTrackSuccess = MutableLiveData<Event<Unit>>()
    val eventAddTrackSuccess: LiveData<Event<Unit>>
        get() = _eventAddTrackSuccess

    private val _eventAddTrackFail = MutableLiveData<Event<String>>()
    val eventAddTrackFail: LiveData<Event<String>>
        get() = _eventAddTrackFail

    val code = MutableLiveData("")
    val codeIsValid = code.map {
        if (it.isBlank()) {
            return@map true
        }
        return@map Correios.validateCode(it)
    }

    val name = MutableLiveData("")
    val nameIsValid = code.map { it.isNotBlank() }

    private val _eventQR = MutableLiveData<Event<Unit>>()
    val eventQR: LiveData<Event<Unit>>
        get() = _eventQR

    private val _eventInputNameError = MutableLiveData<Event<Unit>>()
    val eventInputNameError: LiveData<Event<Unit>>
        get() = _eventInputNameError

    private val _eventInputCodeError = MutableLiveData<Event<Unit>>()
    val eventInputCodeError: LiveData<Event<Unit>>
        get() = _eventInputCodeError

    private val _eventInputSuccess = MutableLiveData<Event<Unit>>()
    val eventInputSuccess: LiveData<Event<Unit>>
        get() = _eventInputSuccess

    fun handleQRButton() {
        _eventQR.value = Event(Unit)
    }

    fun handleOKButton() {
        validateInputsAndAddProduct()
    }

    private fun getTrackInfo(code: String, observation: String) {
        viewModelScope.launch {
            val apiResponse = repository.getTrackInfoFromAPI(code, observation)
            apiResponse.item?.let {
                _eventAddTrackSuccess.value = Event(Unit)
                if (it.item.isDelivered && preferences.getAutoMove()) {
                    repository.archiveTrack(code)
                }
            } ?: run {
                _eventAddTrackFail.value = Event(apiResponse.response)
            }
        }
    }

    fun handleBackButton() {
        _eventBackButtonNavigation.value = Event(Unit)
    }

    private fun validateInputsAndAddProduct() {
        val code = code.value!!
        val name = name.value!!

        if (!Correios.validateCode(code)) {
            _eventInputCodeError.value = Event(Unit)
            return
        }

        if (name.isEmpty()) {
            _eventInputNameError.value = Event(Unit)
            return
        }

        _eventInputSuccess.value = Event(Unit)
        getTrackInfo(code, name)
    }

    fun addCodeByQR(qr: String) {
        if (Correios.validateCode(qr)) {
            code.value = qr
        }
    }

    fun startRefreshWorker(activity: Activity) {
        RefreshTracksWorker.startWorker(activity.application, preferences)
    }

    fun handleClipboardOnResume(clipData: ClipData?) {
        clipData?.let {
            val currentCode = code.value!!
            // Ignore clipboard if current code are not empty or already a valid code
            if (currentCode.isNotEmpty() && Correios.validateCode(currentCode)) {
                return
            }

            val correiosCode = it.getItemAt(0)?.text?.toString()?.trim() ?: return
            if (Correios.validateCode(correiosCode)) {
                code.value = correiosCode
            }
        }
    }
}
