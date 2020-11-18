package dev.keader.correiostracker.repository

import androidx.lifecycle.LiveData
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.database.toItemWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import timber.log.Timber
import javax.inject.Inject

class TrackingRepository @Inject constructor(private val database: TrackingDatabaseDAO) {

    suspend fun archiveTrack(trackCode: String) {
        withContext(Dispatchers.IO) {
            database.archiveTrack(trackCode)
        }
    }

    suspend fun unArchiveTrack(trackCode: String) {
        withContext(Dispatchers.IO) {
            database.unArchiveTrack(trackCode)
        }
    }

    suspend fun deleteTrack(itemWithTracks: ItemWithTracks) {
        withContext(Dispatchers.IO) {
            database.deleteItemWithTracks(itemWithTracks)
        }
    }

    // Only the items not archived
    fun getAllItemsWithTracks(): LiveData<List<ItemWithTracks>> {
        return database.getAllItemsWithTracks()
    }

    fun getAllArchivedItemsWithTracks(): LiveData<List<ItemWithTracks>> {
        return database.getAllArchivedItemsWithTracks()
    }

    suspend fun getTrackInfoFromAPI(code: String, observation: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val itemWithTracks = Correios.getTrack(code).toItemWithTracks()
                itemWithTracks.item.name = observation
                database.insertItemWithTracks(itemWithTracks)
                Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")
                return@withContext true
            } catch (e: IOException) {
                Timber.e(e.message, e.stackTrace)
                return@withContext false
            }
        }
    }

    fun getItemWithTracks(itemCode: String): LiveData<ItemWithTracks> {
        return database.getItemWithTracks(itemCode)
    }

    suspend fun refreshTracks() {
        withContext(Dispatchers.IO) {
            val items = database.getAllItemsToRefresh()
            for (item in items) {
                try {
                    val updatedItem = Correios.getTrack(item.item.code).toItemWithTracks()
                    updatedItem.item.name = item.item.name
                    database.insertItemWithTracks(updatedItem)
                } catch (e: IOException) {
                    Timber.e(e.message, e.stackTrace)
                }
            }
        }
    }
}
