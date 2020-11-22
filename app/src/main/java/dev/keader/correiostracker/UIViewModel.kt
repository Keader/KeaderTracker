package dev.keader.correiostracker

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UIViewModel @ViewModelInject constructor() : ViewModel() {

    private val _bottomNavVisibility = MutableLiveData(View.VISIBLE)
    val bottomNavVisibility: LiveData<Int>
        get() = _bottomNavVisibility

    private val _qrCodeResult = MutableLiveData<String?>()
    val qrCodeResult: LiveData<String?>
        get() = _qrCodeResult

    fun setQrCode(value: String) {
        _qrCodeResult.value = value
    }

    fun finishQrCode() {
        _qrCodeResult.value = null
    }

    fun setBottomNavVisibility(visibility: Int) {
        _bottomNavVisibility.value = visibility
    }
}
