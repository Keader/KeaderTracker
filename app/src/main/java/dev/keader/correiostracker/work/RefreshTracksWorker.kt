package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LiveData
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.notification.LocalNotification
import dev.keader.correiostracker.repository.TrackingRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshTracksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val preferences: PreferencesManager,
    val repository: TrackingRepository) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshTracksWorker"

        fun startWorker(context: Context, preferences: PreferencesManager) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val frequency = preferences.getFrequency()

            val repeating = PeriodicWorkRequestBuilder<RefreshTracksWorker>(frequency, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

            Timber.i("Worker starting service.")
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    repeating)
        }

        fun getWorkInfoLiveData(context: Context) : LiveData<List<WorkInfo>> {
            return WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(WORK_NAME)
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
        val autoMove = preferences.getAutoMove()
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
