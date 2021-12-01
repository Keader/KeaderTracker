package dev.keader.correiostracker.repository

import androidx.lifecycle.LiveData
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.sharedapiobjects.ItemWithTracks
import timber.log.Timber
import javax.inject.Inject

class TrackingRepository @Inject constructor(private val database: TrackingDatabaseDAO) {

    suspend fun archiveTrack(trackCode: String) {
            database.archiveTrack(trackCode)
    }

    suspend fun unArchiveTrack(trackCode: String) {
            database.unArchiveTrack(trackCode)
    }

    suspend fun archiveAllDeliveredItems() {
            database.archiveAllDeliveredItems()
    }

    suspend fun deleteTrack(itemWithTracks: ItemWithTracks) {
            database.deleteItemWithTracks(itemWithTracks)
    }

    suspend fun updateItemName(code: String, newName: String) {
            database.updateItemName(code, newName)
    }

    // Only the items not archived
    fun getAllItemsWithTracks(): LiveData<List<ItemWithTracks>> {
        return database.getAllItemsWithTracks()
    }

    fun getAllArchivedItemsWithTracks(): LiveData<List<ItemWithTracks>> {
        return database.getAllArchivedItemsWithTracks()
    }

    suspend fun getTrackInfoFromAPI(code: String, observation: String): String {
            try {
                val itemWithTracks = Correios.getProduct(code)
                itemWithTracks.item.name = observation
                database.insertItemWithTracks(itemWithTracks)
                Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")
                return ""
            } catch (e: Exception) {
                Timber.e(e)
                return e.message ?: ""
            }
    }

    fun getItemWithTracks(itemCode: String): LiveData<ItemWithTracks> {
        return database.getItemWithTracks(itemCode)
    }

    suspend fun refreshTracks(): UpdateItem {
            val notificationList = mutableListOf<ItemWithTracks>()
            val items = database.getAllItemsToRefresh()
            if (items.isEmpty())
                return UpdateItem(false, notificationList)

            // Fetch Info from API
            items.forEach { oldItem ->
                try {
                    val updatedItem = Correios.getProduct(oldItem.item.code)
                    updatedItem.item.name = oldItem.item.name
                    if (updatedItem.tracks.size > oldItem.tracks.size)
                        notificationList.add(updatedItem)
                    else if (oldItem.item.isWaitingPost && !updatedItem.item.isWaitingPost)
                        notificationList.add(updatedItem) // Posted
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }

            // Update items in Database. It run in a single transaction
            database.insertItemWithTracks(notificationList)
            return UpdateItem(true, notificationList)
        }
}

data class UpdateItem(
    val hasItemsInDBToUpdate: Boolean,
    val updateList: List<ItemWithTracks>
)
