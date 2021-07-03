package com.maxtyler.blocked.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: List<Setting>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: Setting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(colourChoice: ColourChoice)

    @Query("DELETE from colourchoice")
    suspend fun deleteColourChoice()

    @Query("SELECT * FROM setting")
    fun getSettings(): Flow<List<Setting>>

    @Transaction
    @Query("SELECT * FROM colourchoice")
    fun getColour(): Flow<ChosenColours?>

    @Query("DELETE FROM setting")
    suspend fun clearSettings()

}