package com.spotshare.domain.model

data class Reel(
    val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val videoUrl: String,
    val thumbnailUrl: String,
    val caption: String,
    val location: Location?,
    val locationName: String?,
    val audioName: String?,
    val likes: Int,
    val commentCount: Int,
    val shareCount: Int,
    val viewCount: Int,
    val timestamp: Long,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val tags: List<String>
)
