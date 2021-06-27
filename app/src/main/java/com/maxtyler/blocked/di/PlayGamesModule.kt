package com.maxtyler.blocked.di

import android.content.Context
import com.maxtyler.blocked.repository.PlayGamesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayGamesModule {
    @Provides
    @Singleton
    fun providePlayGamesRepository(@ApplicationContext context: Context): PlayGamesRepository = PlayGamesRepository(context)
}