package dev.keader.correiostracker.view.addpacket


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO
import dev.keader.correiostracker.database.toItemWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import timber.log.Timber


class AddPacketViewModel(private val database: ItemDatabaseDAO) : ViewModel() {

    private val _eventCancelButtonNavigation = MutableLiveData(false)
    val eventCancelButtonNavigation: LiveData<Boolean>
        get() = _eventCancelButtonNavigation

    private val _eventAddTrack = MutableLiveData<Boolean?>()
    val eventAddTrack: LiveData<Boolean?>
        get() = _eventAddTrack

    private val _eventCheckInputs = MutableLiveData(false)
    val eventCheckInputs: LiveData<Boolean>
        get() = _eventCheckInputs

    fun handleOKButton() {
        _eventCheckInputs.value = true
    }

    fun handleCheckOK(code: String, observation: String) {
        _eventCheckInputs.value = false
        getTrackInfo(code, observation)
    }

    private fun getTrackInfo(code: String, observation: String) {
        viewModelScope.launch {
            _eventAddTrack.value = getTrackInfoFromAPI(code, observation)
        }
    }

    private suspend fun getTrackInfoFromAPI(code: String, observation: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var itemWithTracks = Correios.getTrack(code).toItemWithTracks()
                itemWithTracks.item.name = observation
                database.insertItemWithTracks(itemWithTracks)
                Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")
                return@withContext true
            } catch (e: IOException) {
                Timber.e(e.message, e.stackTrace)
                return@withContext false
            }
        }
    }

    fun handleCheckFail() {
        _eventCheckInputs.value = false

    }

    fun handleCancelButton() {
        _eventCancelButtonNavigation.value = true
    }

    fun handleAddEventFinish() {
        _eventAddTrack.value = null
    }

    fun handleCancelButtonEventFinish() {
        _eventCancelButtonNavigation.value = false
    }
}