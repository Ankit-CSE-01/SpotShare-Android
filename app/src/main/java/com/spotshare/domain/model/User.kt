package com.spotshare.domain.model

data class User(
    val uid: String,
    val userName: String,
    val displayName: String,
    val email: String,
    val bio: String? = null,
    val location: String? = null,
    val profilePicUrl: String? = null,
    val website: String? = null,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val isPrivate: Boolean = false,
    val savedPosts: List<String> = emptyList(),
    val fcmToken: String? = null
)
