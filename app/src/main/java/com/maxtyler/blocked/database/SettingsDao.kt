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

    @Transaction
    @Query("SELECT * FROM setting, colourchoice")
    fun get(): Flow<SettingsAndColours?>

    @Query("DELETE FROM setting")
    suspend fun clearSettings()

}