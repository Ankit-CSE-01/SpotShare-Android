package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val mediaUrl: String,
    val mediaType: String, // IMAGE or VIDEO
    val duration: Long,
    val locationLat: Double?,
    val locationLng: Double?,
    val locationName: String?,
    val timestamp: Long,
    val expiresAt: Long, // Auto-delete after 24h (WorkManager - UNIT II)
    val views: String, // JSON array of user IDs
    val isViewed: Boolean
)
