package com.spotshare.domain.usecase

import com.spotshare.domain.model.Spot
import com.spotshare.domain.repository.SpotRepository
import javax.inject.Inject

class GetSpotDetailsUseCase @Inject constructor(
    private val repository: SpotRepository
) {
    suspend operator fun invoke(spotId: String): Spot? {
        return repository.getSpotById(spotId)
    }
}
