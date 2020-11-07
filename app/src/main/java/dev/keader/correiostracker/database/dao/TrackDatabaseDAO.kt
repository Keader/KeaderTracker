package dev.keader.correiostracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.keader.correiostracker.database.Track

@Dao
interface TrackDatabaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tracks: Track)

    @Query("SELECT * from Track WHERE trackUid = :key")
    suspend fun get(key: Long): Track?

    @Query("SELECT * from Track WHERE itemCode = :code")
    suspend fun getAllTrackOfItem(code: String): List<Track>

    @Query("DELETE FROM Track")
    suspend fun clear()
}