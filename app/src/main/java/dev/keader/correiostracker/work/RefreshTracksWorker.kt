package dev.keader.correiostracker.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.keader.correiostracker.repository.TrackingRepository
import timber.log.Timber
import javax.inject.Inject

class RefreshTracksWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters) : CoroutineWorker(context, params) {

    @Inject lateinit var repository: TrackingRepository

    companion object {
        const val WORK_NAME = "RefreshTracksWorker"
    }

    override suspend fun doWork(): Result {
        Timber.e("Worker chamado com sucesso")
        repository.refreshTracks()
        return Result.success()
    }

}
