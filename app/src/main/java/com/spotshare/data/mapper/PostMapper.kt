package com.spotshare.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spotshare.data.local.entity.PostEntity
import com.spotshare.domain.model.Location
import com.spotshare.domain.model.Media
import com.spotshare.domain.model.Post

private val gson = Gson()

fun PostEntity.toPost(): Post {
    val mediaUrlsList: List<String> = gson.fromJson(mediaUrls, object : TypeToken<List<String>>() {}.type)
    val mediaTypesList: List<String> = gson.fromJson(mediaTypes, object : TypeToken<List<String>>() {}.type)
    val tagsList: List<String> = gson.fromJson(tags, object : TypeToken<List<String>>() {}.type)

    val mediaList = mediaUrlsList.zip(mediaTypesList).map { (url, type) ->
        Media(
            url = url,
            type = com.spotshare.domain.model.MediaType.valueOf(type)
        )
    }

    return Post(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        media = mediaList,
        caption = caption,
        location = if (locationLat != null && locationLng != null) Location(locationLat, locationLng, locationName) else null,
        locationName = locationName,
        likes = likes,
        commentCount = commentCount,
        timestamp = timestamp,
        isLiked = isLiked,
        isSaved = isSaved,
        tags = tagsList,
        rating = rating
    )
}

fun Post.toEntity(): PostEntity {
    val mediaUrlsJson = gson.toJson(media.map { it.url })
    val mediaTypesJson = gson.toJson(media.map { it.type.name })
    val tagsJson = gson.toJson(tags)

    return PostEntity(
        id = id,
        userId = userId,
        userName = userName,
        userProfilePic = userProfilePic,
        mediaUrls = mediaUrlsJson,
        mediaTypes = mediaTypesJson,
        caption = caption,
        locationLat = location?.latitude,
        locationLng = location?.longitude,
        locationName = locationName,
        likes = likes,
        commentCount = commentCount,
        timestamp = timestamp,
        isLiked = isLiked,
        isSaved = isSaved,
        tags = tagsJson,
        rating = rating
    )
}
