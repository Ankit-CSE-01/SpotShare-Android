package com.spotshare.domain.repository

import com.spotshare.domain.model.Review
import com.spotshare.domain.model.Spot
import kotlinx.coroutines.flow.Flow

interface SpotRepository {
    fun getNearbySpots(latitude: Double, longitude: Double, radius: Double): Flow<List<Spot>>
    suspend fun getSpotById(id: String): Spot?
    suspend fun addSpot(spot: Spot): Result<Unit>
    suspend fun addReview(review: Review): Result<Unit>
    fun getReviewsForSpot(spotId: String): Flow<List<Review>>
    suspend fun uploadImage(imageUri: String): Result<String>
    fun getSavedSpots(userId: String): Flow<List<Spot>>
    fun getCreatedSpots(userId: String): Flow<List<Spot>>
    suspend fun toggleSaveSpot(userId: String, spotId: String): Result<Unit>
    fun isSpotSaved(userId: String, spotId: String): Flow<Boolean>
}
