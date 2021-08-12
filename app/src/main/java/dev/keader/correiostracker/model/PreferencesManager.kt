package dev.keader.correiostracker.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.keader.correiostracker.R
import dev.keader.correiostracker.model.PreferencesManager.PreferencesKeys.AUTO_MOVE
import dev.keader.correiostracker.model.PreferencesManager.PreferencesKeys.DARK_THEME
import dev.keader.correiostracker.model.PreferencesManager.PreferencesKeys.FREQUENCY
import dev.keader.correiostracker.model.PreferencesManager.PreferencesKeys.SCAN_INTRO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_SPINNER_POSITION = 2
const val DEFAULT_FREQUENCY_VALUE = 60L
const val DEFAULT_THEME_VALUE = false
const val DEFAULT_AUTO_MOVE = false
const val DEFAULT_SCAN_INTRO = true

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "user_settings",
        produceMigrations = ::sharedPreferencesMigration
    )

    private fun sharedPreferencesMigration(context: Context) =
        listOf(SharedPreferencesMigration(context, context.getString(R.string.shared_pref_name)))

    private val dataStore = context.dataStore

    val autoMoveFlow = dataStore.data.map {
        it[AUTO_MOVE] ?: DEFAULT_AUTO_MOVE
    }

    val frequencyFlow = dataStore.data.map {
        it[FREQUENCY] ?: DEFAULT_SPINNER_POSITION
    }

    val darkThemeFlow = dataStore.data.map {
        it[DARK_THEME] ?: DEFAULT_THEME_VALUE
    }

    val scanIntroFlow = dataStore.data.map {
        it[SCAN_INTRO] ?: DEFAULT_SCAN_INTRO
    }

    suspend fun saveAutoMove(autoMove: Boolean) {
        dataStore.edit {
            it[AUTO_MOVE] = autoMove
        }
    }

    suspend fun saveFrequency(frequencyIndex: Int) {
        dataStore.edit {
            it[FREQUENCY] = frequencyIndex
        }
    }

    suspend fun saveDarkTheme(darkTheme: Boolean) {
        dataStore.edit {
            it[DARK_THEME] = darkTheme
        }
    }

    suspend fun saveScanIntro(scanIntro: Boolean) {
        dataStore.edit {
            it[SCAN_INTRO] = scanIntro
        }
    }

    fun getFrequency() = runBlocking {
        when (frequencyFlow.first()) {
            0 -> 15L
            1 -> 30L
            2 -> 60L
            3 -> 120L
            4 -> 240L
            5 -> 480L
            else -> DEFAULT_FREQUENCY_VALUE
        }
    }

    fun getFrequencyPosition() = runBlocking {
        frequencyFlow.first()
    }

    fun getScanIntro() = runBlocking {
        scanIntroFlow.first()
    }

    fun getAutoMove() = runBlocking {
        autoMoveFlow.first()
    }

    fun getDarkTheme() = runBlocking {
        darkThemeFlow.first()
    }

    private object PreferencesKeys {
        val AUTO_MOVE = booleanPreferencesKey("PREF_AUTOMOVE")
        val FREQUENCY = intPreferencesKey("PREF_FREQUENCY_POS")
        val DARK_THEME = booleanPreferencesKey("PREF_THEME")
        val SCAN_INTRO = booleanPreferencesKey("PREF_SCAN_INTRO")
    }
}
