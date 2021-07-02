package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Save::class, Score::class, Colours::class, ColourChoice::class, Setting::class],
    version = 8,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class BlockedDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun saveDao(): SaveDao
    abstract fun scoreDao(): ScoreDao
    abstract fun coloursDao(): ColoursDao
}