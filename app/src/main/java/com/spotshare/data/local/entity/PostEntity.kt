package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val mediaUrls: String, // JSON array of Media
    val mediaTypes: String, // JSON array of MediaType
    val caption: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val locationName: String?,
    val likes: Int,
    val commentCount: Int,
    val timestamp: Long,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val tags: String, // JSON array
    val rating: Float? // For place rating (Custom Rating Bar - UNIT I)
)
