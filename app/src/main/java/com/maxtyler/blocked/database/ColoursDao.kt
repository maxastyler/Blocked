package com.maxtyler.blocked.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ColoursDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(colours: List<Colours>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(colours: Colours)

    @Query("DELETE FROM colours WHERE coloursId=:id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM colours")
    fun getColours(): Flow<List<Colours>>
}