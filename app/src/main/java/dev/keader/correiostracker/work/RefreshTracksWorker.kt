package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import dev.keader.correiostracker.repository.TrackingRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RefreshTracksWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val repository: TrackingRepository) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshTracksWorker"

        fun startWorker(ctx: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val repeating = PeriodicWorkRequestBuilder<RefreshTracksWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            Timber.i("Worker starting service.")
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeating)
        }

        fun stopWorker(ctx: Context) {
            Timber.i("Stopping worker, has no tracks do watch")
            WorkManager.getInstance(ctx).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        Timber.i("Worker updating tracks")
        // If has no tracks, cancel worker
        if (!repository.refreshTracks())
            stopWorker(applicationContext)

        return Result.success()
    }
}
