package dev.keader.correiostracker

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.keader.correiostracker.util.Event

class UIViewModel @ViewModelInject constructor() : ViewModel() {

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
