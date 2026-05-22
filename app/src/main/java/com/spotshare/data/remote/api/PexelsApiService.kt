package com.spotshare.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PexelsApiService {
    
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): PexelsResponse

    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Header("Authorization") apiKey: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): PexelsResponse

    @GET("videos/search")
    suspend fun searchVideos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): PexelsVideoResponse

    companion object {
        const val BASE_URL = "https://api.pexels.com/"
    }
}

// Photo Response models
data class PexelsResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val photos: List<PexelsPhoto>,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("next_page") val nextPage: String?
)

data class PexelsPhoto(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("photographer_id") val photographerId: Long,
    @SerializedName("avg_color") val avgColor: String,
    val src: PexelsSrc,
    val liked: Boolean,
    val alt: String
)

data class PexelsSrc(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)

// Video Response models
data class PexelsVideoResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val total_results: Int,
    val url: String,
    val videos: List<PexelsVideo>
)

data class PexelsVideo(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String, // Thumbnail
    val duration: Int,
    val user: PexelsUser,
    @SerializedName("video_files") val videoFiles: List<PexelsVideoFile>
)

data class PexelsUser(
    val id: Long,
    val name: String,
    val url: String
)

data class PexelsVideoFile(
    val id: Int,
    val quality: String,
    val file_type: String,
    val width: Int?,
    val height: Int?,
    val link: String
)
