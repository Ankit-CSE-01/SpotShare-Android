package com.spotshare.presentation.screens.addspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory
import com.spotshare.domain.usecase.AddSpotUseCase
import com.spotshare.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddSpotViewModel @Inject constructor(
    private val addSpotUseCase: AddSpotUseCase,
    private val auth: FirebaseAuth,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddSpotUiState>(AddSpotUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _locationState = MutableStateFlow(LocationState())
    val locationState = _locationState.asStateFlow()

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _locationState.update { it.copy(isLoading = true) }
            val location = locationHelper.getCurrentLocation()
            if (location != null) {
                val address = locationHelper.getAddressFromLocation(location.latitude, location.longitude)
                _locationState.update { 
                    it.copy(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = address,
                        isLoading = false
                    )
                }
            } else {
                _locationState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addSpot(
        name: String,
        description: String,
        category: SpotCategory,
        imageUris: List<String>,
        tags: List<String>
    ) {
        val location = _locationState.value
        if (location.latitude == null || location.longitude == null) {
            _uiState.value = AddSpotUiState.Error("Location is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddSpotUiState.Loading
            val spot = Spot(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                category = category,
                latitude = location.latitude,
                longitude = location.longitude,
                imageUrls = emptyList(),
                rating = 0.0,
                reviewCount = 0,
                createdBy = auth.currentUser?.uid ?: "",
                createdAt = System.currentTimeMillis(),
                address = location.address,
                tags = tags
            )
            
            val result = addSpotUseCase(spot, imageUris)
            if (result.isSuccess) {
                _uiState.value = AddSpotUiState.Success
            } else {
                _uiState.value = AddSpotUiState.Error(result.exceptionOrNull()?.message ?: "Failed to add spot")
            }
        }
    }
}

data class LocationState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val isLoading: Boolean = false
)

sealed class AddSpotUiState {
    object Idle : AddSpotUiState()
    object Loading : AddSpotUiState()
    object Success : AddSpotUiState()
    data class Error(val message: String) : AddSpotUiState()
}
