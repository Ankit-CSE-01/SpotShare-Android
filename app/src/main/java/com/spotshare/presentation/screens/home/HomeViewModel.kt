package com.spotshare.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory
import com.spotshare.domain.usecase.GetNearbySpots
import com.spotshare.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNearbySpots: GetNearbySpots,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<SpotCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DISTANCE)
    val sortOrder = _sortOrder.asStateFlow()

    private var allSpots = listOf<Spot>()
    private var userLocation: Pair<Double, Double>? = null

    init {
        loadSpots()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { filterAndSortSpots() }
            .launchIn(viewModelScope)
    }

    fun loadSpots() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            // Try to get current location for nearby spots
            val location = locationHelper.getCurrentLocation()
            val lat = location?.latitude ?: 0.0
            val lng = location?.longitude ?: 0.0
            userLocation = Pair(lat, lng)

            getNearbySpots(lat, lng, 10.0)
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
                }
                .collect { spots ->
                    allSpots = spots
                    filterAndSortSpots()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // filterAndSortSpots() // Handled by debounce
    }

    fun selectCategory(category: SpotCategory?) {
        _selectedCategory.value = category
        filterAndSortSpots()
    }

    fun onSortOrderChange(order: SortOrder) {
        _sortOrder.value = order
        filterAndSortSpots()
    }

    private fun filterAndSortSpots() {
        var filtered = allSpots
        
        // Filter by category
        _selectedCategory.value?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        // Filter by search query
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(_searchQuery.value, ignoreCase = true) || 
                it.description.contains(_searchQuery.value, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(_searchQuery.value, ignoreCase = true) }
            }
        }

        // Sort
        filtered = when (_sortOrder.value) {
            SortOrder.DISTANCE -> {
                userLocation?.let { loc ->
                    filtered.sortedBy { spot ->
                        locationHelper.getDistanceBetween(loc.first, loc.second, spot.latitude, spot.longitude)
                    }
                } ?: filtered
            }
            SortOrder.RATING -> filtered.sortedByDescending { it.rating }
            SortOrder.NEWEST -> filtered.sortedByDescending { it.createdAt }
        }

        _uiState.value = HomeUiState.Success(filtered)
    }
}

enum class SortOrder {
    DISTANCE, RATING, NEWEST
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val spots: List<Spot>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
