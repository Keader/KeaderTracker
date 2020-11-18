package dev.keader.correiostracker.view.trackdetail

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackDetailViewModel @ViewModelInject constructor(
        private val repository: TrackingRepository) : ViewModel() {

    private lateinit var _trackCode: String
    val trackCode: String
        get() = _trackCode
    private lateinit var _trackItem : LiveData<ItemWithTracks>
    val trackItem: LiveData<ItemWithTracks>
        get() = _trackItem
    private lateinit var _isArchived: LiveData<Boolean>
    val isArchived: LiveData<Boolean>
        get() = _isArchived

    fun setTrackCode(code: String) {
        _trackCode = code
        _trackItem = repository.getItemWithTracks(_trackCode)
        _isArchived = Transformations.map(_trackItem) {
            it?.let {
                it.item.isArchived
            }
        }
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
        when(view.getTag(R.id.tag_archived)) {
            TAG_VALUE_UNARCHIVED -> handleArchiveTrack()
            TAG_VALUE_ARCHIVED -> handleUnArchiveTrack()
        }
    }

    private fun handleArchiveTrack() {
        viewModelScope.launch {
            repository.archiveTrack(trackCode)
            _eventFloatButton.value = true
        }
    }

    private fun handleUnArchiveTrack() {
        viewModelScope.launch {
            repository.unArchiveTrack(trackCode)
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
