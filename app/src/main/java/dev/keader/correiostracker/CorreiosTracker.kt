package dev.keader.correiostracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.model.tree.CorreiosTrackerDebugTree
import dev.keader.correiostracker.model.tree.CrashlyticsTree
import dev.keader.correiostracker.notification.LocalNotification
import dev.keader.correiostracker.repository.TrackingRepository
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

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(CorreiosTrackerDebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        delayedInit()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun delayedInit() {
        applicationScope.launch {
            LocalNotification.createNotificationChannel(applicationContext)
            RefreshTracksWorker.startWorker(applicationContext, preferences)
        }
    }
}
