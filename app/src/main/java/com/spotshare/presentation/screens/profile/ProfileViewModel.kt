package com.spotshare.presentation.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.User
import com.spotshare.domain.repository.SpotRepository
import com.spotshare.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: SpotRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _createdSpots = MutableStateFlow<List<Spot>>(emptyList())
    val createdSpots = _createdSpots.asStateFlow()

    private val _userReels = MutableStateFlow<List<com.spotshare.domain.model.Reel>>(emptyList())
    val userReels = _userReels.asStateFlow()

    val isOwnProfile: Boolean = userId == null || userId == auth.currentUser?.uid

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val targetUserId = userId ?: auth.currentUser?.uid
        if (targetUserId != null) {
            userRepository.getUser(targetUserId).onEach {
                _user.value = it
            }.launchIn(viewModelScope)
            
            viewModelScope.launch {
                repository.getCreatedSpots(targetUserId).collect {
                    _createdSpots.value = it
                }
            }
        }
    }

    fun updateProfile(name: String, username: String, bio: String, website: String) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch
            val updatedUser = currentUser.copy(
                displayName = name,
                userName = username,
                bio = bio,
                website = website
            )
            userRepository.updateProfile(updatedUser)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun toggleFollow() {
        // Follow logic
    }
}
