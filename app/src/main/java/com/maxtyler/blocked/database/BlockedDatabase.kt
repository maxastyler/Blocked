package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings

@Database(
    entities = [UISettings::class, Save::class, Score::class, ColourSettings::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(SaveConverters::class, ScoreConverters::class, ColourSettingsConverters::class)
abstract class BlockedDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun saveDao(): SaveDao
    abstract fun scoreDao(): ScoreDao
    abstract fun colourSettingsDao(): ColourSettingsDao
}