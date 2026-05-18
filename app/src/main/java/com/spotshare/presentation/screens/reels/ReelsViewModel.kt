package com.spotshare.presentation.screens.reels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.BuildConfig
import com.spotshare.data.remote.api.PexelsApiService
import com.spotshare.domain.model.Reel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val pexelsApi: PexelsApiService
) : ViewModel() {

    private val _reels = MutableStateFlow<List<Reel>>(emptyList())
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    init {
        loadReelsFromPexels()
    }

    fun loadReelsFromPexels() {
        viewModelScope.launch {
            try {
                val response = pexelsApi.searchVideos(
                    apiKey = BuildConfig.PEXELS_API_KEY,
                    query = "travel vlog nature",
                    perPage = 15
                )
                val mappedReels = response.videos.map { video ->
                    // Find an appropriate video file (prefer hd/mp4)
                    val videoFile = video.videoFiles.find { it.quality == "hd" } ?: video.videoFiles.first()
                    
                    Reel(
                        id = video.id.toString(),
                        userId = video.user.id.toString(),
                        userName = video.user.name,
                        userProfilePic = null,
                        videoUrl = videoFile.link,
                        thumbnailUrl = video.image,
                        caption = "Amazing experience at this spot!",
                        location = null,
                        locationName = "Scenic Route",
                        audioName = "Original Audio",
                        likes = (1000..5000).random(),
                        commentCount = (50..200).random(),
                        shareCount = (20..100).random(),
                        viewCount = (5000..20000).random(),
                        timestamp = System.currentTimeMillis(),
                        isLiked = false,
                        isSaved = false,
                        tags = emptyList()
                    )
                }
                _reels.value = mappedReels
            } catch (e: Exception) {
                Log.e("ReelsViewModel", "Error fetching Pexels videos", e)
            }
        }
    }

    fun likeReel(reelId: String) {
        _reels.update { list ->
            list.map { if (it.id == reelId) it.copy(isLiked = !it.isLiked, likes = if (it.isLiked) it.likes - 1 else it.likes + 1) else it }
        }
    }
}
