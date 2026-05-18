package com.spotshare.data.remote.dto

import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory

data class SpotDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrls: List<String> = emptyList(),
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val createdBy: String = "",
    val createdAt: Long = 0,
    val address: String? = null,
    val tags: List<String> = emptyList()
) {
    fun toSpot(): Spot {
        return Spot(
            id = id,
            name = name,
            description = description,
            category = try { SpotCategory.valueOf(category) } catch (e: Exception) { SpotCategory.OTHER },
            latitude = latitude,
            longitude = longitude,
            imageUrls = imageUrls,
            rating = rating,
            reviewCount = reviewCount,
            createdBy = createdBy,
            createdAt = createdAt,
            address = address,
            tags = tags
        )
    }
}

fun Spot.toDto(): SpotDto {
    return SpotDto(
        id = id,
        name = name,
        description = description,
        category = category.name,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrls,
        rating = rating,
        reviewCount = reviewCount,
        createdBy = createdBy,
        createdAt = createdAt,
        address = address,
        tags = tags
    )
}
