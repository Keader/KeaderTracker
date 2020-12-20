package dev.keader.correiostracker.view.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val repository: TrackingRepository): ViewModel() {

    private val _eventNavigateBack = MutableLiveData(false)
    val eventNavigateBack: LiveData<Boolean>
        get() = _eventNavigateBack

    private val _eventNavigateOK = MutableLiveData(false)
    val eventNavigateOK: LiveData<Boolean>
        get() = _eventNavigateOK

    fun handleArchiveAllCurrentItems() {
        this.viewModelScope.launch {
            repository.archiveAllDeliveredItems()
        }
    }

    fun onCancelButtonClicked() {
        _eventNavigateBack.value = true
    }

    fun onCancelButtonEventFinished() {
        _eventNavigateBack.value = false
    }

    fun onOKButtonClicked() {
        _eventNavigateOK.value = true
    }

    fun onOKButtonEventFinished() {
        _eventNavigateOK.value = false
    }

}
