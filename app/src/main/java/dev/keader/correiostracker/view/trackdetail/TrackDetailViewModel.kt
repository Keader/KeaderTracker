package dev.keader.correiostracker.view.trackdetail


import android.view.View
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.R
import dev.keader.correiostracker.model.Event
import dev.keader.correiostracker.model.combineWith
import dev.keader.correiostracker.model.setValueIfNew
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.interfaces.TrackHistoryButtonTypes
import dev.keader.correiostracker.view.interfaces.TrackHistoryButtonTypes.*
import dev.keader.correiostracker.view.interfaces.TrackHistoryListener
import dev.keader.sharedapiobjects.ItemWithTracks
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val repository: TrackingRepository
) : ViewModel(), TrackHistoryListener {

    companion object {
        const val TAG_VALUE_UNARCHIVED = 0
        const val TAG_VALUE_ARCHIVED = 1
    }

    val trackCode = MutableLiveData<String>()
    val trackItem = trackCode.switchMap { repository.getItemWithTracks(it) }
    val isArchived = trackItem.switchMap { mapFabStatus(it.item.isArchived) }

    private val _eventNavigateAfterDelete = MutableLiveData<Event<Unit>>()
    val eventNavigateAfterDelete: LiveData<Event<Unit>>
        get() = _eventNavigateAfterDelete

    private val _eventNavigateBack = MutableLiveData<Event<Unit>>()
    val eventNavigateBack: LiveData<Event<Unit>>
        get() = _eventNavigateBack

    private val _eventCopyButton = MutableLiveData<Event<ItemWithTracks>>()
    val eventCopyButton: LiveData<Event<ItemWithTracks>>
        get() = _eventCopyButton

    private val _eventDeleteButton = MutableLiveData<Event<ItemWithTracks>>()
    val eventDeleteButton: LiveData<Event<ItemWithTracks>>
        get() = _eventDeleteButton

    private val _eventShareButton = MutableLiveData<Event<Unit>>()
    val eventShareButton: LiveData<Event<Unit>>
        get() = _eventShareButton

    private val _eventEditButton = MutableLiveData<Event<Unit>>()
    val eventEditButton: LiveData<Event<Unit>>
        get() = _eventEditButton

    private val _eventItemArchived = MutableLiveData<Event<Unit>>()
    val eventItemArchived: LiveData<Event<Unit>>
        get() = _eventItemArchived

    private val _eventItemUnArchived = MutableLiveData<Event<Unit>>()
    val eventItemUnArchived: LiveData<Event<Unit>>
        get() = _eventItemUnArchived

    private val _eventSwipeRefreshRunning = MutableLiveData(false)

    private val _isRefreshWorkRunning = repository.getRefreshWorkInfo()

    val eventRefreshRunning = _isRefreshWorkRunning.combineWith(_eventSwipeRefreshRunning) { a, b ->
        return@combineWith a == true || b == true
    }

    fun setTrackCode(code: String) {
        trackCode.setValueIfNew(code)
    }

    private fun mapFabStatus(isArchived: Boolean): LiveData<FabStatus> {
        val liveData = MutableLiveData<FabStatus>()
        if (isArchived) {
            liveData.value = FabStatus(
                TAG_VALUE_ARCHIVED,
                R.drawable.ic_track_delivery,
                R.color.primaryColor
            )
        } else {
            liveData.value = FabStatus(
                TAG_VALUE_UNARCHIVED,
                R.drawable.ic_delivered_outline,
                R.color.secondaryColor
            )
        }
        return liveData
    }

    fun handleDeleteItem(itemWithTracks: ItemWithTracks) {
        viewModelScope.launch {
            repository.deleteTrack(itemWithTracks)
            _eventNavigateAfterDelete.value = Event(Unit)
        }
    }

    fun onRefreshCalled() {
        _eventSwipeRefreshRunning.value = true
        viewModelScope.launch {
            repository.refreshTrack(trackItem.value)
            _eventSwipeRefreshRunning.value = false
        }
    }

    fun onFloatButtonClicked(view: View) {
        when (view.getTag(R.id.tag_archived)) {
            TAG_VALUE_UNARCHIVED -> handleArchiveTrack()
            TAG_VALUE_ARCHIVED -> handleUnArchiveTrack()
        }
    }

    private fun handleArchiveTrack() {
        viewModelScope.launch {
            trackCode.value?.let {
                repository.archiveTrack(it)
                _eventItemArchived.value = Event(Unit)
            }
        }
    }

    private fun handleUnArchiveTrack() {
        viewModelScope.launch {
            trackCode.value?.let {
                repository.unArchiveTrack(it)
                _eventItemUnArchived.value = Event(Unit)
            }
        }
    }

    fun handleWithUpdateName(newName: String) {
        if (newName.isEmpty() || newName.isBlank())
            return

        viewModelScope.launch {
            trackCode.value?.let {
                repository.updateItemName(it, newName)
            }
        }
    }

    override fun onItemClicked(item: ItemWithTracks, id: TrackHistoryButtonTypes) {
        when (id) {
            BUTTON_BACK -> _eventNavigateBack.value = Event(Unit)
            BUTTON_COPY -> _eventCopyButton.value = Event(item)
            BUTTON_DELETE -> _eventDeleteButton.value = Event(item)
            BUTTON_SHARE -> _eventShareButton.value = Event(Unit)
            BUTTON_EDIT -> _eventEditButton.value = Event(Unit)
        }
    }

    data class FabStatus(
        val tag: Int,
        val imgSrc: Int,
        val bgColor: Int
    )
}
