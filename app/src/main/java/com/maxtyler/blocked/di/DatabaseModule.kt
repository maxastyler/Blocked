package com.maxtyler.blocked.di

import android.content.Context
import androidx.room.Room
import com.maxtyler.blocked.database.BlockedDatabase
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
    fun provideBlockedDatabase(@ApplicationContext application: Context): BlockedDatabase {
        return Room.databaseBuilder(
            application, BlockedDatabase::class.java, "blocked_database"
        ).enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }
}