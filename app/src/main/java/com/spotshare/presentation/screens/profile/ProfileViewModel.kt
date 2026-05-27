package com.spotshare.presentation.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.User
import com.spotshare.domain.repository.ChatRepository
import com.spotshare.domain.repository.UserRepository
import com.spotshare.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val locationHelper: LocationHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _navigateToChat = MutableStateFlow<String?>(null)
    val navigateToChat = _navigateToChat.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _locationLoading = MutableStateFlow(false)
    val locationLoading = _locationLoading.asStateFlow()

    val isOwnProfile: Boolean = userId == null || userId == auth.currentUser?.uid

    init {
        loadUserProfile()
        
        // Observe picked location from Map via SavedStateHandle
        savedStateHandle.getStateFlow<List<Double>?>("picked_location", null)
            .onEach { coords ->
                if (coords != null && coords.size >= 2) {
                    setMapLocation(coords[0], coords[1])
                }
                savedStateHandle["picked_location"] = null
            }
            .launchIn(viewModelScope)
    }

    private fun loadUserProfile() {
        val targetUserId = userId ?: auth.currentUser?.uid
        if (targetUserId != null) {
            userRepository.getUser(targetUserId).onEach {
                _user.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun updateProfile(name: String, username: String, bio: String, website: String, location: String?) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch
            val updatedUser = currentUser.copy(
                displayName = name,
                userName = username,
                bio = bio,
                website = website,
                location = location
            )
            userRepository.updateProfile(updatedUser)
        }
    }

    fun fetchCurrentLocation(onResult: (String) -> Unit) {
        viewModelScope.launch {
            _locationLoading.value = true
            val loc = locationHelper.getCurrentLocation()
            if (loc != null) {
                val address = locationHelper.getAddressFromLocation(loc.latitude, loc.longitude)
                onResult(address ?: "Unknown Location")
            }
            _locationLoading.value = false
        }
    }

    fun setMapLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            _locationLoading.value = true
            val address = locationHelper.getAddressFromLocation(lat, lng)
            val currentUser = _user.value
            if (currentUser != null) {
                _user.value = currentUser.copy(location = address ?: "Picked Spot")
            }
            _locationLoading.value = false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun startChat() {
        val targetUserId = userId ?: return
        viewModelScope.launch {
            chatRepository.getOrCreateChat(targetUserId).onSuccess { chatId ->
                _navigateToChat.value = chatId
            }
        }
    }

    fun onChatNavigated() {
        _navigateToChat.value = null
    }

    fun toggleFollow() {
        // Follow logic
    }
}
