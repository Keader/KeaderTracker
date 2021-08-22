package dev.keader.correiostracker.repository

import androidx.lifecycle.LiveData
import dev.keader.correiosapi.Correios
import dev.keader.correiosapi.UNKNOWN_TYPE
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

    suspend fun archiveAllDeliveredItems() {
        withContext(Dispatchers.IO) {
            database.archiveAllDeliveredItems()
        }
    }

    suspend fun deleteTrack(itemWithTracks: ItemWithTracks) {
        withContext(Dispatchers.IO) {
            database.deleteItemWithTracks(itemWithTracks)
        }
    }

    suspend fun updateItemName(code: String, newName: String) {
        withContext(Dispatchers.IO) {
            database.updateItemName(code, newName)
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
                val itemWithTracks = Correios.getTrackFromSite(code).toItemWithTracks()
                itemWithTracks.item.name = observation
                database.insertItemWithTracks(itemWithTracks)
                Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")

                // Log Unknown Codes
                if (itemWithTracks.item.type.contains(UNKNOWN_TYPE))
                    Timber.e(itemWithTracks.item.type)

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

    suspend fun refreshTracks(): UpdateItem {
        return withContext(Dispatchers.IO) {
            val notificationList = mutableListOf<ItemWithTracks>()
            val items = database.getAllItemsToRefresh()
            if (items.isEmpty())
                return@withContext UpdateItem(false, notificationList)

            // Fetch Info from API
            items.forEach { oldItem ->
                try {
                    val updatedItem = Correios.getTrackFromSite(oldItem.item.code).toItemWithTracks()
                    updatedItem.item.name = oldItem.item.name
                    if (updatedItem.tracks.size != oldItem.tracks.size)
                        notificationList.add(updatedItem)
                    // object posted, yay
                    else if (oldItem.item.isWaitingPost && !updatedItem.item.isWaitingPost)
                        notificationList.add(updatedItem)

                } catch (e: IOException) {
                    Timber.e(e.message, e.stackTrace)
                }
            }

            // Update items in Database. It run in a single transaction
            database.insertItemWithTracks(notificationList)
            return@withContext UpdateItem(true, notificationList)
        }
    }
}

data class UpdateItem(
    val hasItemsInDBToUpdate: Boolean,
    val updateList: List<ItemWithTracks>
)
