package com.spotshare.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spotshare.data.local.entity.ReelEntity
import com.spotshare.domain.model.Location
import com.spotshare.domain.model.Reel

private val gson = Gson()

fun ReelEntity.toReel(): Reel {
    val tagsList: List<String> = gson.fromJson(tags, object : TypeToken<List<String>>() {}.type)

    return Reel(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        caption = caption,
        location = if (locationLat != null && locationLng != null) Location(locationLat, locationLng, locationName) else null,
        locationName = locationName,
        audioName = audioName,
        likes = likes,
        commentCount = commentCount,
        shareCount = shareCount,
        viewCount = viewCount,
        timestamp = timestamp,
        isLiked = isLiked,
        isSaved = isSaved,
        tags = tagsList
    )
}

fun Reel.toEntity(): ReelEntity {
    val tagsJson = gson.toJson(tags)

    return ReelEntity(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        caption = caption,
        locationLat = location?.latitude,
        locationLng = location?.longitude,
        locationName = locationName,
        audioName = audioName,
        likes = likes,
        commentCount = commentCount,
        shareCount = shareCount,
        viewCount = viewCount,
        timestamp = timestamp,
        isLiked = isLiked,
        isSaved = isSaved,
        tags = tagsJson
    )
}
