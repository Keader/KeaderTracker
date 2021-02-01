package dev.keader.correiostracker.view.trackdetail

import android.view.View
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val repository: TrackingRepository) : ViewModel() {

    val trackCode = MutableLiveData<String>()

    val trackItem = Transformations.switchMap(trackCode) { trackCode ->
        repository.getItemWithTracks(trackCode)
    }

    val isArchived = Transformations.map(trackItem) { track ->
        track?.let {
            it.item.isArchived
        }
    }

    fun setTrackCode(code: String) {
        trackCode.setValueIfNew(code)
    }

    fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
        if (this.value != newValue) value = newValue
    }

    private val _eventFloatButton = MutableLiveData<Boolean?>()

    // if true is archive, if false is unArchive
    val eventFloatButton: LiveData<Boolean?>
        get() = _eventFloatButton

    private val _eventDeleteButton = MutableLiveData(false)
    val eventDeleteButton: LiveData<Boolean>
        get() = _eventDeleteButton

    fun onDeleteButtonClicked(itemWithTracks: ItemWithTracks) {
        handleDeleteItem(itemWithTracks)
    }

    private fun handleDeleteItem(itemWithTracks: ItemWithTracks) {
        viewModelScope.launch {
            repository.deleteTrack(itemWithTracks)
            _eventDeleteButton.value = true
        }
    }

    fun onFloatButtonPressed(view: View) {
        when (view.getTag(R.id.tag_archived)) {
            TAG_VALUE_UNARCHIVED -> handleArchiveTrack()
            TAG_VALUE_ARCHIVED -> handleUnArchiveTrack()
        }
    }

    private fun handleArchiveTrack() {
        viewModelScope.launch {
            repository.archiveTrack(trackCode.value!!)
            _eventFloatButton.value = true
        }
    }

    private fun handleUnArchiveTrack() {
        viewModelScope.launch {
            repository.unArchiveTrack(trackCode.value!!)
            _eventFloatButton.value = false
        }
    }

    fun onFloatButtonComplete() {
        _eventFloatButton.value = null
    }

    fun onDeleteButtonComplete() {
        _eventDeleteButton.value = false
    }
}
