package com.spotshare.data.remote.dto

import com.spotshare.domain.model.User

data class UserDto(
    val uid: String = "",
    val userName: String = "",
    val displayName: String = "",
    val email: String = "",
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
) {
    fun toUser(): User {
        return User(
            uid = uid,
            userName = userName,
            displayName = displayName,
            email = email,
            bio = bio,
            location = location,
            profilePicUrl = profilePicUrl,
            website = website,
            postsCount = postsCount,
            followersCount = followersCount,
            followingCount = followingCount,
            isFollowing = isFollowing,
            isPrivate = isPrivate,
            savedPosts = savedPosts,
            fcmToken = fcmToken
        )
    }
}

fun User.toDto(): UserDto {
    return UserDto(
        uid = uid,
        userName = userName,
        displayName = displayName,
        email = email,
        bio = bio,
        location = location,
        profilePicUrl = profilePicUrl,
        website = website,
        postsCount = postsCount,
        followersCount = followersCount,
        followingCount = followingCount,
        isFollowing = isFollowing,
        isPrivate = isPrivate,
        savedPosts = savedPosts,
        fcmToken = fcmToken
    )
}
