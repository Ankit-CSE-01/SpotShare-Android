package com.spotshare.domain.usecase

import com.spotshare.domain.model.Spot
import com.spotshare.domain.repository.SpotRepository
import javax.inject.Inject

class AddSpotUseCase @Inject constructor(
    private val repository: SpotRepository
) {
    suspend operator fun invoke(spot: Spot, imageUris: List<String>): Result<Unit> {
        return try {
            val uploadedUrls = imageUris.map { uri ->
                repository.uploadImage(uri).getOrThrow()
            }
            val spotWithUrls = spot.copy(imageUrls = uploadedUrls)
            repository.addSpot(spotWithUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
