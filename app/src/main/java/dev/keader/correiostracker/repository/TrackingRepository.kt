package dev.keader.correiostracker.repository

import androidx.lifecycle.LiveData
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.sharedapiobjects.ItemWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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

    suspend fun getTrackInfoFromAPI(code: String, observation: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val itemWithTracks = Correios.getProduct(code)
                itemWithTracks.item.name = observation
                database.insertItemWithTracks(itemWithTracks)
                Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")
                return@withContext ""
            } catch (e: Exception) {
                Timber.e(e)
                return@withContext e.message ?: ""
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
                    val updatedItem = Correios.getProduct(oldItem.item.code)
                    updatedItem.item.name = oldItem.item.name
                    if (updatedItem.tracks.size > oldItem.tracks.size)
                        notificationList.add(updatedItem)
                    else if (oldItem.item.isWaitingPost && !updatedItem.item.isWaitingPost)
                        notificationList.add(updatedItem) // Posted

                    delay(10000L)
                } catch (e: Exception) {
                    Timber.e(e)
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
