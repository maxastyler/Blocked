package com.maxtyler.blocked.database

import androidx.room.*
import com.maxtyler.blocked.game.ColourSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface ColourSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(colourSettings: ColourSettings)

    @Delete
    suspend fun delete(colourSettings: ColourSettings)

    @Query("SELECT * FROM coloursettings")
    fun getColours(): Flow<List<ColourSettings>>
}