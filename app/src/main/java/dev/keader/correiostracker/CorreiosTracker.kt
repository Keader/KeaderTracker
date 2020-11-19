package dev.keader.correiostracker

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CorreiosTracker : Application(),  Configuration.Provider {

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var repository: TrackingRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        delayedInit()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun delayedInit() {
        applicationScope.launch {
            RefreshTracksWorker.startWorker(applicationContext)
        }
    }
}
