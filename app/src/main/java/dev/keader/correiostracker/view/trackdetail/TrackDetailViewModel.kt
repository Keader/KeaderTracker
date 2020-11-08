package dev.keader.correiostracker.view.trackdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrackDetailViewModel : ViewModel() {

    private val _trackTitle = MutableLiveData<String>()
    val trackTitle: LiveData<String>
        get() = _trackTitle

    private val _trackCode = MutableLiveData<String>()
    val trackCode: LiveData<String>
        get() = _trackCode

    private val _trackStartLocale = MutableLiveData<String>()
    val trackStartLocale: LiveData<String>
        get() = _trackStartLocale

    private val _trackPostedAt = MutableLiveData<String>()
    val trackPostedAt: LiveData<String>
        get() = _trackPostedAt

    private val _trackLastUpdate = MutableLiveData<String>()
    val trackLastUpdate: LiveData<String>
        get() = _trackLastUpdate

    private val _eventFloatButton = MutableLiveData(false)
    val eventFloatButton: LiveData<Boolean>
        get() = _eventFloatButton

    fun onFloatButtonPressed() {
        _eventFloatButton.value = true
    }

    fun onFloatButtonComplete() {
        _eventFloatButton.value = false
    }
}