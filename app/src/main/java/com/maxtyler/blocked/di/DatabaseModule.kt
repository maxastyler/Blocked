package com.maxtyler.blocked.di

import android.content.Context
import androidx.room.Room
import com.maxtyler.blocked.database.SaveDatabase
import com.maxtyler.blocked.database.ScoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideScoreDatabase(@ApplicationContext application: Context): ScoreDatabase {
        return Room.databaseBuilder(
            application, ScoreDatabase::class.java, "score_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideSaveDatabase(@ApplicationContext application: Context): SaveDatabase {
        return Room.databaseBuilder(
            application, SaveDatabase::class.java, "save_database"
        ).fallbackToDestructiveMigration().build()
    }
}