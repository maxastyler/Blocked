package com.maxtyler.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Save::class], version = 1, exportSchema = true)
abstract class SettingsDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
}