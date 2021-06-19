package com.example.blocked.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Score::class], version = 1, exportSchema = true)
abstract class ScoreDatabase: RoomDatabase() {

    abstract fun scoreDao(): ScoreDao
}