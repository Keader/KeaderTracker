package dev.keader.correiostracker

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.util.Event
import javax.inject.Inject

@HiltViewModel
class UIViewModel @Inject constructor() : ViewModel() {

    private val _bottomNavVisibility = MutableLiveData(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    private val _qrCodeResult = MutableLiveData<Event<String>>()
    val qrCodeResult: LiveData<Event<String>>
        get() = _qrCodeResult

    fun setQrCode(value: String) {
        _qrCodeResult.value = Event(value)
    }

    fun setBottomNavVisibility(visibility: Int) {
        _bottomNavVisibility.value = visibility
    }
}
