package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val videoUrl: String,
    val thumbnailUrl: String,
    val caption: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val locationName: String?,
    val audioName: String?,
    val likes: Int,
    val commentCount: Int,
    val shareCount: Int,
    val viewCount: Int,
    val timestamp: Long,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val tags: String // JSON
)
