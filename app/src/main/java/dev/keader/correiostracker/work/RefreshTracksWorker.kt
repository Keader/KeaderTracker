package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.keader.correiostracker.repository.TrackingRepository
import timber.log.Timber

class RefreshTracksWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val repository: TrackingRepository) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshTracksWorker"

        fun stopWorker(ctx: Context) {
            Timber.e("Stopping worker, has no tracks do watch")
            WorkManager.getInstance(ctx).cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        Timber.e("Worker updating tracks")
        // If has no tracks, cancel worker
        if (!repository.refreshTracks())
            stopWorker(applicationContext)

        return Result.success()
    }
}
