package dev.keader.correiostracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO
import dev.keader.correiostracker.work.RefreshTracksWorker
import dev.keader.sharedapiobjects.ItemWithTracks
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class TrackingRepository @Inject constructor(
    private val database: TrackingDatabaseDAO,
    private val workManager: WorkManager
) {

    private companion object {
        const val DEFAULT_ERROR = "Ocorreu um erro na adição do seu rastreamento. Verifique se o código de rastreamento está correto."
        var retryCount = 0
    }

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

    suspend fun getTrackInfoFromAPI(code: String, observation: String): APIResponse {
        return try {
            retryCount = 0
            val itemWithTracks = getProductWithRetry(code)
            itemWithTracks.item.name = observation
            database.insertItemWithTracks(itemWithTracks)
            Timber.i("Track ${itemWithTracks.item.name} added with code: ${itemWithTracks.item.code}")
            APIResponse(itemWithTracks, "")
        } catch (e: Exception) {
            Timber.e(e)
            APIResponse(null, e.message ?: DEFAULT_ERROR)
        }
    }

    fun getItemWithTracks(itemCode: String): LiveData<ItemWithTracks> {
        return database.getItemWithTracks(itemCode)
    }

    suspend fun refreshTrack(trackCode: ItemWithTracks?) {
        if (trackCode != null) {
            val newItem = fetchItemWithTracks(trackCode)
            if (newItem != null) {
                database.insertItemWithTracks(newItem)
            }
        }
    }

    suspend fun refreshTracks(): UpdateItem {
        val notificationList = mutableListOf<ItemWithTracks>()
        val items = database.getAllItemsToRefresh()
        if (items.isEmpty())
            return UpdateItem(false, notificationList)

        // Fetch Info from API
        items.forEach { oldItem ->
            val newItem: ItemWithTracks? = fetchItemWithTracks(oldItem)
            if (newItem != null) {
                notificationList.add(newItem)
            }
        }

        // Update items in Database.
        database.insertItemWithTracks(notificationList)
        return UpdateItem(true, notificationList)
    }

    private suspend fun fetchItemWithTracks(
        oldItem: ItemWithTracks
    ): ItemWithTracks? {
        val newItem: ItemWithTracks? = try {
            retryCount = 0
            val updatedItem = getProductWithRetry(oldItem.item.code)
            updatedItem.item.name = oldItem.item.name
            if (updatedItem.tracks.size > oldItem.tracks.size ||
                oldItem.item.isWaitingPost && !updatedItem.item.isWaitingPost
            )// Posted
                updatedItem
            else null
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
        return newItem
    }

    private suspend fun getProductWithRetry(code: String): ItemWithTracks {
        // Correios sometimes return a Unexpected End of Stream Exception
        // It's a server side error and has nothing that we can do about it
        // So... time to do some retries :(
        return try {
            Correios.getProduct(code)
        } catch (e: Exception) {
            if (retryCount >= 5)
                throw e

            ++retryCount
            delay(1000L)
            getProductWithRetry(code)
        }
    }

    fun getRefreshWorkInfo(): LiveData<Boolean> {
        return workManager.getWorkInfosForUniqueWorkLiveData(RefreshTracksWorker.WORK_NAME).map {
            it.firstOrNull()?.state == WorkInfo.State.RUNNING
        }
    }
}

data class APIResponse(
    val item: ItemWithTracks?,
    val response: String
)

data class UpdateItem(
    val hasItemsInDBToUpdate: Boolean,
    val updateList: List<ItemWithTracks>
)
