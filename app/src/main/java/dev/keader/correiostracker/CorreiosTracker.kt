package dev.keader.correiostracker

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CorreiosTracker : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        delayedInit()
    }

    private fun delayedInit() {
        setupRecurringWork()
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
                /*
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }*/
            .build()

        val repeating = PeriodicWorkRequestBuilder<RefreshTracksWorker>(16, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        Timber.e("Worker iniciado com sucesso")

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshTracksWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            repeating)
    }
}
