package dev.keader.correiostracker.hilt

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keader.correiostracker.database.TrackingDatabase
import javax.inject.Singleton

@Module
    @InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackingDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TrackingDatabase::class.java,
            "correios_tracker_database")
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackingDao(database: TrackingDatabase) = database.itemDatabaseDAO
}
