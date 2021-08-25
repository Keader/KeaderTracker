package dev.keader.correiostracker.view.trackdetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.BuildConfig
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.databinding.DialogEditItemNameBinding
import dev.keader.correiostracker.model.toFile
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

    fun onFloatButtonClicked(view: View) {
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

    fun onShareButtonClicked(context: Context, view: View) {
        val file = view.drawToBitmap().toFile(context)
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/jpg"
        context.startActivity(intent)
    }

    fun onEditButtonClicked(activity: Activity) {
        val binding = DialogEditItemNameBinding.inflate(activity.layoutInflater)
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.edit_package_description))
            .setView(binding.root)
            .setPositiveButton(activity.getString(R.string.OK)) { dialog, _ ->
                binding.inputText.text?.let { newName ->
                    handleWithUpdateName(newName.toString())
                }
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun handleWithUpdateName(newName : String) {
        if (newName.isEmpty() || newName.isBlank())
            return

        viewModelScope.launch {
            repository.updateItemName(trackCode.value!!, newName)
        }
    }
}
