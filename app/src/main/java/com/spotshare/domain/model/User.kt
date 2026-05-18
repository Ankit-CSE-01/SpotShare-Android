package com.spotshare.domain.model

data class User(
    val uid: String,
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
    val savedPosts: List<String>,
    val fcmToken: String?
)
