package dev.keader.correiostracker.view.capture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.util.Event
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(): ViewModel() {
    fun onPermissionsDenied() {

    }
}