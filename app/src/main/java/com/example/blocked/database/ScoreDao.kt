package com.example.blocked.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert
    fun insert(score: Score)

    @Delete
    fun delete(score: Score)

    @Query("SELECT * FROM score")
    fun getAll(): Flow<Score>
}