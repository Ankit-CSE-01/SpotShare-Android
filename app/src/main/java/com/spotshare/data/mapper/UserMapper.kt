package com.spotshare.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spotshare.data.local.entity.UserEntity
import com.spotshare.domain.model.User

private val gson = Gson()

fun UserEntity.toUser(): User {
    val savedPostsList: List<String> = gson.fromJson(savedPosts, object : TypeToken<List<String>>() {}.type)

    return User(
        uid = uid,
        userName = userName,
        displayName = displayName,
        email = email,
        bio = bio,
        profilePicUrl = profilePicUrl,
        website = website,
        postsCount = postsCount,
        followersCount = followersCount,
        followingCount = followingCount,
        isFollowing = isFollowing,
        isPrivate = isPrivate,
        savedPosts = savedPostsList,
        fcmToken = fcmToken
    )
}

fun User.toEntity(): UserEntity {
    val savedPostsJson = gson.toJson(savedPosts)

    return UserEntity(
        uid = uid,
        userName = userName,
        displayName = displayName,
        email = email,
        bio = bio,
        profilePicUrl = profilePicUrl,
        website = website,
        postsCount = postsCount,
        followersCount = followersCount,
        followingCount = followingCount,
        isFollowing = isFollowing,
        isPrivate = isPrivate,
        savedPosts = savedPostsJson,
        fcmToken = fcmToken
    )
}
