package dev.keader.correiostracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keader.correiostracker.database.M1TO2
import dev.keader.correiostracker.database.M2TO3
import dev.keader.correiostracker.database.M3TO4
import dev.keader.correiostracker.database.M4TO5
import dev.keader.correiostracker.database.M5TO6
import dev.keader.correiostracker.database.TrackingDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "correios_tracker_database"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackingDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TrackingDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(M1TO2, M2TO3, M3TO4, M4TO5, M5TO6)
            .build()
    }

    @Provides
    @Singleton
    fun provideTrackingDao(database: TrackingDatabase) = database.itemDatabaseDAO
}
