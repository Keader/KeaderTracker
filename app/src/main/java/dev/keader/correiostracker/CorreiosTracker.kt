package dev.keader.correiostracker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import dev.keader.correiostracker.firebase.CrashlyticsTree
import dev.keader.correiostracker.notification.LocalNotification
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.settings.DEFAULT_FREQUENCY_VALUE
import dev.keader.correiostracker.view.settings.DEFAULT_THEME_VALUE
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class CorreiosTracker : Application(), Configuration.Provider {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var repository: TrackingRepository

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        else
            Timber.plant(CrashlyticsTree())

        val sharedPref = getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE)
        val darkTheme = sharedPref.getBoolean(getString(R.string.preference_theme), DEFAULT_THEME_VALUE)
        if (darkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        delayedInit()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun delayedInit() {
        applicationScope.launch {
            LocalNotification.createNotificationChannel(applicationContext)
            RefreshTracksWorker.startWorker(applicationContext)
        }
    }
}
