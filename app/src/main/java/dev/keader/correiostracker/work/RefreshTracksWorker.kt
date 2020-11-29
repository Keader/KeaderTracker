package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.notification.LocalNotification
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
                .build()

            val repeating = PeriodicWorkRequestBuilder<RefreshTracksWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            Timber.i("Worker starting service.")
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                repeating)
        }

        fun stopWorker(ctx: Context) {
            Timber.i("Stopping worker, has no tracks do watch.")
            WorkManager.getInstance(ctx).cancelUniqueWork(WORK_NAME)
        }

        fun sendNotifications(updateList: List<ItemWithTracks>, context: Context) {
            Timber.i("Sending notifications...")
            for (item in updateList) {
                LocalNotification.sendNotification(context, item)
            }
        }
    }

    override suspend fun doWork(): Result {
        Timber.i("Worker updating tracks.")
        val result = repository.refreshTracks()
        // if dont have items in DB to update, stop worker
        if (!result.hasItemsInDBToUpdate)
            stopWorker(applicationContext)
        else if (result.updateList.isNotEmpty())
            sendNotifications(result.updateList, applicationContext)

        return Result.success()
    }
}
