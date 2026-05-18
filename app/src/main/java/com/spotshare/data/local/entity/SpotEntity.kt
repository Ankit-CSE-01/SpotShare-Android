package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory

@Entity(tableName = "spots")
data class SpotEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: String, // Stored as comma-separated string
    val rating: Double,
    val reviewCount: Int,
    val createdBy: String,
    val createdAt: Long,
    val address: String?,
    val tags: String // Stored as comma-separated string
) {
    fun toSpot(): Spot {
        return Spot(
            id = id,
            name = name,
            description = description,
            category = try { SpotCategory.valueOf(category) } catch (e: Exception) { SpotCategory.OTHER },
            latitude = latitude,
            longitude = longitude,
            imageUrls = if (imageUrls.isEmpty()) emptyList() else imageUrls.split(","),
            rating = rating,
            reviewCount = reviewCount,
            createdBy = createdBy,
            createdAt = createdAt,
            address = address,
            tags = if (tags.isEmpty()) emptyList() else tags.split(",")
        )
    }
}

fun Spot.toEntity(): SpotEntity {
    return SpotEntity(
        id = id,
        name = name,
        description = description,
        category = category.name,
        latitude = latitude,
        longitude = longitude,
        imageUrls = imageUrls.joinToString(","),
        rating = rating,
        reviewCount = reviewCount,
        createdBy = createdBy,
        createdAt = createdAt,
        address = address,
        tags = tags.joinToString(",")
    )
}
