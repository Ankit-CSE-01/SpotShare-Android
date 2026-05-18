package com.spotshare.presentation.screens.spotdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.Review
import com.spotshare.domain.model.Spot
import com.spotshare.domain.usecase.GetSpotDetailsUseCase
import com.spotshare.domain.repository.SpotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SpotDetailViewModel @Inject constructor(
    private val getSpotDetailsUseCase: GetSpotDetailsUseCase,
    private val repository: SpotRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val spotId: String = checkNotNull(savedStateHandle["spotId"])

    private val _uiState = MutableStateFlow<SpotDetailUiState>(SpotDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    init {
        loadSpotDetails()
        loadReviews()
        observeSavedStatus()
    }

    private fun observeSavedStatus() {
        auth.currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                repository.isSpotSaved(userId, spotId).collect {
                    _isSaved.value = it
                }
            }
        }
    }

    private fun loadSpotDetails() {
        viewModelScope.launch {
            val spot = getSpotDetailsUseCase(spotId)
            if (spot != null) {
                _uiState.value = SpotDetailUiState.Success(spot)
            } else {
                _uiState.value = SpotDetailUiState.Error("Spot not found")
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            repository.getReviewsForSpot(spotId).collect {
                _reviews.value = it
            }
        }
    }

    fun submitReview(rating: Int, comment: String) {
        viewModelScope.launch {
            val review = Review(
                id = UUID.randomUUID().toString(),
                spotId = spotId,
                userId = auth.currentUser?.uid ?: "",
                userName = auth.currentUser?.displayName ?: "User",
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            repository.addReview(review)
        }
    }

    fun toggleSave() {
        auth.currentUser?.uid?.let { userId ->
            viewModelScope.launch {
                repository.toggleSaveSpot(userId, spotId)
            }
        }
    }
}

sealed class SpotDetailUiState {
    object Loading : SpotDetailUiState()
    data class Success(val spot: Spot) : SpotDetailUiState()
    data class Error(val message: String) : SpotDetailUiState()
}
