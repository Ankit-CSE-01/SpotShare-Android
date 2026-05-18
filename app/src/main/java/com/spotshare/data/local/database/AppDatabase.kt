package com.spotshare.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spotshare.data.local.dao.*
import com.spotshare.data.local.entity.*

@Database(
    entities = [
        SpotEntity::class,
        PostEntity::class,
        StoryEntity::class,
        ReelEntity::class,
        UserEntity::class,
        CommentEntity::class,
        MessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val spotDao: SpotDao
    abstract val postDao: PostDao
    abstract val storyDao: StoryDao
    abstract val reelDao: ReelDao
    abstract val messageDao: MessageDao

    companion object {
        const val DATABASE_NAME = "spotshare_db"
    }
}
