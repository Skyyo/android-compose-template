package com.skyyo.template.application.injection

import android.content.Context
import androidx.room.Room
import com.skyyo.template.application.persistance.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "name-database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideCatsDao(appDatabase: AppDatabase) = appDatabase.catsDao()

    @Singleton
    @Provides
    fun provideCatsRemoteKeysDao(appDatabase: AppDatabase) = appDatabase.catsRemoteKeysDao()
}
