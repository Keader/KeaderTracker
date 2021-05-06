package dev.keader.correiostracker

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.util.Event
import dev.keader.correiostracker.view.capture.CodeDetectionActions
import javax.inject.Inject

@HiltViewModel
class UIViewModel @Inject constructor() : ViewModel(), CodeDetectionActions {

    private val _bottomNavVisibility = MutableLiveData(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    private val _qrCodeResult = MutableLiveData<Event<String>>()
    val qrCodeResult: LiveData<Event<String>>
        get() = _qrCodeResult

    private val _onQrCodeDetected = MutableLiveData<Event<Unit>>()
    val onQrCodeDetected: LiveData<Event<Unit>>
        get() = _onQrCodeDetected

    fun setBottomNavVisibility(visibility: Int) {
        _bottomNavVisibility.value = visibility
    }

    override fun onCodeDetected(code: String, source: Int) {
        _qrCodeResult.value = Event(code)
    }
}
