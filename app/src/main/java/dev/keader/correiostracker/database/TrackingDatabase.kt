package dev.keader.correiostracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.keader.correiostracker.database.dao.ItemDatabaseDAO
import dev.keader.correiostracker.database.dao.TrackDatabaseDAO

@Database(entities = [Item::class, Track::class], version = 1, exportSchema = false)
abstract class TrackingDatabase : RoomDatabase() {
    abstract val itemDatabaseDAO: ItemDatabaseDAO
    abstract val trackDatabaseDAO: TrackDatabaseDAO

    companion object {
        @Volatile
        private var INSTANCE: TrackingDatabase? = null

        fun getInstance(context: Context): TrackingDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrackingDatabase::class.java,
                        "correios_tracker_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}