package dev.keader.correiostracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.keader.correiostracker.database.Item
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.Track

@Dao
interface ItemDatabaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tracks: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(track: Track)

    @Transaction
    suspend fun insertItemWithTracks(item: ItemWithTracks) {
        insert(item.item)
        item.tracks.forEach {
            insertTracks(it)
        }
    }

    @Query("UPDATE Item SET isArchived = 1 WHERE code = :code")
    suspend fun archiveTrack(code: String)

    @Query("UPDATE Item SET isArchived = 0 WHERE code = :code")
    suspend fun unArchiveTrack(code: String)

    @Transaction
    @Query("SELECT * FROM Item WHERE code = :code")
    fun getTrackingWithTracks(code: String): LiveData<ItemWithTracks>

    @Transaction
    @Query("SELECT * FROM Item WHERE isArchived = 0")
    fun getAllItemsWithTracks(): LiveData<List<ItemWithTracks>>

    @Transaction
    @Query("SELECT * FROM Item WHERE isArchived = 1")
    fun getAllArchivedItemsWithTracks(): LiveData<List<ItemWithTracks>>

    @Query("DELETE FROM Item")
    suspend fun clear()
}