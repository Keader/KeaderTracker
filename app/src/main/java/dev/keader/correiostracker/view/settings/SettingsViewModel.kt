package dev.keader.correiostracker.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.repository.TrackingRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: TrackingRepository,
    private val preferences: PreferencesManager
): ViewModel() {

    fun saveAutoMove(autoMove: Boolean) {
        viewModelScope.launch {
            preferences.saveAutoMove(autoMove)
            if (autoMove)
                repository.archiveAllDeliveredItems()
        }
    }

    fun saveDarkTheme(darkTheme: Boolean) {
        viewModelScope.launch {
            preferences.saveDarkTheme(darkTheme)
        }
    }

    fun saveFrequency(frequency: Int) {
        viewModelScope.launch {
            preferences.saveFrequency(frequency)
        }
    }
}
