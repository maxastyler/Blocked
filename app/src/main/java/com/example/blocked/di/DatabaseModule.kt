package com.example.blocked.di

import android.content.Context
import androidx.room.Room
import com.example.blocked.database.ScoreDatabase
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
}