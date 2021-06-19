package com.example.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Score::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class ScoreDatabase: RoomDatabase() {

    abstract fun scoreDao(): ScoreDao
}