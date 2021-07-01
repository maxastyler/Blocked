package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Settings::class, Save::class, Score::class, Colours::class, ColourChoice::class, Setting::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(SaveConverters::class)
abstract class BlockedDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun saveDao(): SaveDao
    abstract fun scoreDao(): ScoreDao
    abstract fun coloursDao(): ColoursDao
}