package com.spotshare.presentation.screens.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.BuildConfig
import com.spotshare.data.remote.api.PexelsApiService
import com.spotshare.data.remote.api.PexelsPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelViewModel @Inject constructor(
    private val pexelsApi: PexelsApiService
) : ViewModel() {
    
    private val apiKey = BuildConfig.PEXELS_API_KEY
    
    private val _travelImages = MutableStateFlow<List<PexelsPhoto>>(emptyList())
    val travelImages: StateFlow<List<PexelsPhoto>> = _travelImages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        loadTravelImages()
    }
    
    fun loadTravelImages() {
        searchImages("travel destinations")
    }
    
    fun searchImages(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = pexelsApi.searchPhotos(
                    apiKey = apiKey,
                    query = query,
                    perPage = 40
                )
                _travelImages.value = response.photos
            } catch (e: Exception) {
                Log.e("TravelViewModel", "Error searching images for $query", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
