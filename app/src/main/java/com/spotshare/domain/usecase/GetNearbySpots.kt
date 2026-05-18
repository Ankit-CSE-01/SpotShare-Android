package com.spotshare.domain.usecase

import com.spotshare.domain.model.Spot
import com.spotshare.domain.repository.SpotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearbySpots @Inject constructor(
    private val repository: SpotRepository
) {
    operator fun invoke(latitude: Double, longitude: Double, radius: Double): Flow<List<Spot>> {
        return repository.getNearbySpots(latitude, longitude, radius)
    }
}
