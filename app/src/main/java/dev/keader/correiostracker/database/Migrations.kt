package dev.keader.correiostracker.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val M1TO2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Item ADD COLUMN isWaitingPost INTEGER NOT NULL DEFAULT 0")
    }
}