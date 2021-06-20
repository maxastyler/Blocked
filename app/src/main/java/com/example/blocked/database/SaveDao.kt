package com.example.blocked.database

import androidx.room.*

@Dao
interface SaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(save: Save)

    @Query("SELECT * FROM save WHERE id=0 LIMIT 1")
    suspend fun get(): Save?

    @Query("DELETE FROM save")
    suspend fun delete()
}