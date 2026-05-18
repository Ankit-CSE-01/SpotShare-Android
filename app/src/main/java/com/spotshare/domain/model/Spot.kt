package com.spotshare.domain.model

data class Spot(
    val id: String,
    val name: String,
    val description: String,
    val category: SpotCategory,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val createdBy: String,
    val createdAt: Long,
    val address: String?,
    val tags: List<String>
)

enum class SpotCategory {
    FOOD, COFFEE, NATURE, ART, VIEWPOINT, HISTORIC, OTHER
}
