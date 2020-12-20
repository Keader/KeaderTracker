package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.preference.PreferenceManager
import androidx.work.*
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.notification.LocalNotification
import dev.keader.correiostracker.repository.TrackingRepository
import dev.keader.correiostracker.view.settings.DEFAULT_FREQUENCY_VALUE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RefreshTracksWorker @WorkerInject constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        val repository: TrackingRepository) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshTracksWorker"
        private val workerScope = CoroutineScope(Dispatchers.Default)

        fun startWorker(context: Context) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()


            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val frequency = sharedPref.getInt(context.getString(R.string.preference_frequency), DEFAULT_FREQUENCY_VALUE).toLong()

            val repeating = PeriodicWorkRequestBuilder<RefreshTracksWorker>(frequency, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

            Timber.i("Worker starting service.")
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    repeating)
        }

        fun stopWorker(ctx: Context) {
            Timber.i("Stopping worker, has no tracks to watch.")
            WorkManager.getInstance(ctx).cancelUniqueWork(WORK_NAME)
        }

        fun sendNotifications(updateList: List<ItemWithTracks>, context: Context) {
            Timber.i("Sending notifications...")
            for (itemWithTracks in updateList)
                LocalNotification.sendNotification(context, itemWithTracks)
        }

        suspend fun archiveItems(updateList: List<ItemWithTracks>, repository: TrackingRepository) {
            Timber.i("Moving items....")
            for (itemWithTracks in updateList) {
                if (itemWithTracks.item.isDelivered)
                    repository.archiveTrack(itemWithTracks.item.code)
            }
        }
    }

    override suspend fun doWork(): Result {
        Timber.i("Worker updating tracks.")
        val result = repository.refreshTracks()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val autoMove = sharedPref.getBoolean(applicationContext.getString(R.string.preference_automove), false)
        // if dont have items in DB to update, stop worker
        if (!result.hasItemsInDBToUpdate)
            stopWorker(applicationContext)
        else if (result.updateList.isNotEmpty()) {
            sendNotifications(result.updateList, applicationContext)
            if (autoMove)
                archiveItems(result.updateList, repository)
        }

        return Result.success()
    }
}
