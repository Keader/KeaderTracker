package dev.keader.correiostracker.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val M1TO2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Item ADD COLUMN isWaitingPost INTEGER NOT NULL DEFAULT 0")
    }
}

val M2TO3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Track ADD COLUMN link TEXT NOT NULL DEFAULT ''")
    }
}
