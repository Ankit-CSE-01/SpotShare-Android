package com.spotshare.domain.model

data class Post(
    val id: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val media: List<Media>,
    val caption: String,
    val location: Location?,
    val locationName: String?,
    val likes: Int,
    val commentCount: Int,
    val timestamp: Long,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val tags: List<String>,
    val rating: Float? = null
)

data class Media(
    val url: String,
    val type: MediaType,
    val thumbnail: String? = null,
    val duration: Long? = null
)

enum class MediaType { IMAGE, VIDEO }

data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
)
