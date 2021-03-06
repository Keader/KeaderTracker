package dev.keader.correiostracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.keader.correiostracker.database.dao.TrackingDatabaseDAO

@Database(entities = [Item::class, Track::class], version = 4, exportSchema = true)
abstract class TrackingDatabase : RoomDatabase() {
    abstract val itemDatabaseDAO: TrackingDatabaseDAO
}
