package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maxtyler.blocked.game.UISettings

@Database(entities = [UISettings::class], version = 1, exportSchema = true)
abstract class SettingsDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
}