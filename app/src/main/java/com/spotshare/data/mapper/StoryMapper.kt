package com.spotshare.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spotshare.data.local.entity.StoryEntity
import com.spotshare.domain.model.Location
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Story

private val gson = Gson()

fun StoryEntity.toStory(): Story {
    val viewsList: List<String> = gson.fromJson(views, object : TypeToken<List<String>>() {}.type)

    return Story(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        mediaUrl = mediaUrl,
        mediaType = MediaType.valueOf(mediaType),
        duration = duration,
        location = if (locationLat != null && locationLng != null) Location(locationLat, locationLng, locationName) else null,
        timestamp = timestamp,
        expiresAt = expiresAt,
        views = viewsList,
        isViewed = isViewed
    )
}

fun Story.toEntity(): StoryEntity {
    val viewsJson = gson.toJson(views)

    return StoryEntity(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        mediaUrl = mediaUrl,
        mediaType = mediaType.name,
        duration = duration,
        locationLat = location?.latitude,
        locationLng = location?.longitude,
        locationName = location?.name,
        timestamp = timestamp,
        expiresAt = expiresAt,
        views = viewsJson,
        isViewed = isViewed
    )
}
