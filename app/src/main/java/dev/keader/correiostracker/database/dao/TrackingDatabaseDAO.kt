package dev.keader.correiostracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.keader.correiostracker.database.Item
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.Track

@Dao
interface TrackingDatabaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tracks: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(track: Track)

    @Transaction
    suspend fun insertItemWithTracks(item: ItemWithTracks) {
        insert(item.item)
        deleteTracks(item.item.code)
        item.tracks.forEach {
            insertTracks(it)
        }
    }

    @Transaction
    suspend fun insertItemWithTracks(tracks: List<ItemWithTracks>) {
        tracks.forEach { item ->
            insert(item.item)
            deleteTracks(item.item.code)
            item.tracks.forEach {
                insertTracks(it)
            }
        }
    }

    @Query("UPDATE Item SET isArchived = 1 WHERE code = :code")
    suspend fun archiveTrack(code: String)

    @Query("UPDATE Item SET isArchived = 0 WHERE code = :code")
    suspend fun unArchiveTrack(code: String)

    @Query("UPDATE Item SET isArchived = 1 WHERE isDelivered = 1" )
    suspend fun archiveAllDeliveredItems()

    @Transaction
    @Query("SELECT * FROM Item WHERE code = :code")
    fun getItemWithTracks(code: String): LiveData<ItemWithTracks>

    @Transaction
    @Query("SELECT * FROM Item WHERE isArchived = 0")
    fun getAllItemsWithTracks(): LiveData<List<ItemWithTracks>>

    @Transaction
    @Query("SELECT * FROM Item WHERE isArchived = 0 AND isDelivered = 0")
    suspend fun getAllItemsToRefresh(): List<ItemWithTracks>

    @Transaction
    @Query("SELECT * FROM Item WHERE isArchived = 1")
    fun getAllArchivedItemsWithTracks(): LiveData<List<ItemWithTracks>>

    @Query("DELETE FROM Item")
    suspend fun clearItems()

    @Query("DELETE FROM Item WHERE code =:code")
    suspend fun deleteItem(code: String)

    @Query("DELETE FROM Track WHERE itemCode =:itemCode")
    suspend fun deleteTracks(itemCode: String)

    @Query("DELETE FROM Track")
    suspend fun clearTracks()

    @Transaction
    suspend fun deleteItemWithTracks(item: ItemWithTracks) {
        val code = item.item.code
        deleteItem(code)
        deleteTracks(code)
    }

}
