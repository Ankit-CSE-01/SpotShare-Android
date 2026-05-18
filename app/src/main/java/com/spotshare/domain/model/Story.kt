package com.spotshare.domain.model

data class Story(
    val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val mediaUrl: String,
    val mediaType: MediaType,
    val duration: Long = 5000,
    val location: Location?,
    val timestamp: Long,
    val expiresAt: Long,
    val views: List<String>,
    val isViewed: Boolean
)

data class StoryGroup(
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val stories: List<Story>,
    val hasUnviewed: Boolean
)
