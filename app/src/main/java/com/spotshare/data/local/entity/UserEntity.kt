package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val userName: String,
    val displayName: String,
    val email: String,
    val bio: String?,
    val profilePicUrl: String?,
    val website: String?,
    val postsCount: Int,
    val followersCount: Int,
    val followingCount: Int,
    val isFollowing: Boolean,
    val isPrivate: Boolean,
    val savedPosts: String, // JSON array
    val fcmToken: String? // For notifications
)
