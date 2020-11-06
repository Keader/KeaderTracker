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

    @Transaction
    @Query("SELECT * FROM Item WHERE code = :code")
    suspend fun getTrackingWithTracks(code: String): LiveData<ItemWithTracks>

    @Transaction
    @Query("SELECT * FROM Item")
    suspend fun getAllItemWithTracks(): LiveData<List<ItemWithTracks>>

    @Query("DELETE FROM Item")
    suspend fun clear()
}