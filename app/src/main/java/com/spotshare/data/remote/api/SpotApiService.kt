package com.spotshare.data.remote.api

import com.spotshare.data.remote.dto.SpotDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotApiService {
    // This is a placeholder for any external API integration
    // The core functionality currently uses Firebase directly
    @GET("spots/nearby")
    suspend fun getNearbySpots(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double
    ): List<SpotDto>
}
