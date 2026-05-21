package com.spotshare.presentation.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.User
import com.spotshare.domain.repository.ChatRepository
import com.spotshare.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String? = savedStateHandle["userId"]

    private val _navigateToChat = MutableStateFlow<String?>(null)
    val navigateToChat = _navigateToChat.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

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
