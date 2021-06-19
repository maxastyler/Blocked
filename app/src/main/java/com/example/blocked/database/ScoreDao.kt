package com.example.blocked.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Delete
    suspend fun delete(score: Score)

    @Query("SELECT * FROM score")
    fun getAll(): Flow<List<Score>>

    @Query("SELECT * FROM score ORDER BY score DESC, date DESC LIMIT :number")
    fun getN(number: Int): Flow<List<Score>>
}