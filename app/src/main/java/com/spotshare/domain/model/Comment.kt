package com.spotshare.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val text: String,
    val likes: Int,
    val isLiked: Boolean,
    val timestamp: Long,
    val replies: List<Comment>?
)
