package com.spotshare.presentation.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.domain.model.Spot
import com.spotshare.domain.repository.SpotRepository
import com.spotshare.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: SpotRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserLocation()
        loadSpots()
    }

    private fun loadUserLocation() {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            location?.let {
                _uiState.update { state ->
                    state.copy(userLocation = Pair(it.latitude, it.longitude))
                }
            }
        }
    }

    fun loadSpots(lat: Double? = null, lng: Double? = null) {
        viewModelScope.launch {
            // Use provided coordinates or fall back to user location or default
            val targetLat = lat ?: _uiState.value.userLocation?.first ?: 0.0
            val targetLng = lng ?: _uiState.value.userLocation?.second ?: 0.0
            
            repository.getNearbySpots(targetLat, targetLng, 10.0)
                .collect { spots ->
                    _uiState.update { it.copy(spots = spots) }
                }
        }
    }

    fun selectSpot(spot: Spot?) {
        _uiState.update { it.copy(selectedSpot = spot) }
    }
}

data class MapUiState(
    val spots: List<Spot> = emptyList(),
    val selectedSpot: Spot? = null,
    val userLocation: Pair<Double, Double>? = null
)
