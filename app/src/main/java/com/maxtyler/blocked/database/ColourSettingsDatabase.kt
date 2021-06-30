package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maxtyler.blocked.game.ColourSettings

@Database(entities = [ColourSettings::class], version = 1, exportSchema = true)
abstract class ColourSettingsDatabase : RoomDatabase() {
    abstract fun colourSettingsDao(): ColourSettingsDao
}