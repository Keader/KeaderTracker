package dev.keader.correiostracker.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val M1TO2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `Item` ADD COLUMN `isWaitingPost` INTEGER NOT NULL DEFAULT 0")
    }
}

val M2TO3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `Track` ADD COLUMN `link` TEXT NOT NULL DEFAULT ''")
    }
}

val M3TO4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Track_itemCode` ON `Track` (`itemCode`)")
    }
}

