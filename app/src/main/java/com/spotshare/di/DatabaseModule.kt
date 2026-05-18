package com.spotshare.di

import android.content.Context
import androidx.room.Room
import com.spotshare.data.local.dao.*
import com.spotshare.data.local.database.AppDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // Added for development as version changed
        .build()
    }

    @Provides
    @Singleton
    fun provideSpotDao(db: AppDatabase): SpotDao = db.spotDao

    @Provides
    @Singleton
    fun providePostDao(db: AppDatabase): PostDao = db.postDao

    @Provides
    @Singleton
    fun provideStoryDao(db: AppDatabase): StoryDao = db.storyDao

    @Provides
    @Singleton
    fun provideReelDao(db: AppDatabase): ReelDao = db.reelDao

    @Provides
    @Singleton
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao
}
