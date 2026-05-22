package com.spotshare.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.spotshare.domain.model.User
import com.spotshare.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        // Basic validation before Firebase attempt
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and Password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                _authState.value = AuthState.Success(result.user?.email ?: email)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(fullName: String, username: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: throw Exception("User creation failed")
                
                // Create user profile in Firestore
                val user = User(
                    uid = uid,
                    userName = username,
                    displayName = fullName,
                    email = email,
                    bio = "I'm new to SpotShare!",
                    profilePicUrl = null,
                    website = null,
                    postsCount = 0,
                    followersCount = 0,
                    followingCount = 0,
                    isFollowing = false,
                    isPrivate = false,
                    savedPosts = emptyList(),
                    fcmToken = null
                )
                
                userRepository.updateProfile(user).getOrThrow()
                
                _authState.value = AuthState.Success(email)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Reset failed")
            }
        }
    }

    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user
                
                if (user != null) {
                    // Check if profile exists
                    val userDoc = userRepository.getUser(user.uid).first()
                    if (userDoc == null) {
                        _authState.value = AuthState.RequiresOnboarding(user.email ?: "")
                    } else {
                        _authState.value = AuthState.Success(user.email ?: "User")
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }

    fun completeOnboarding(fullName: String, username: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val currentUser = auth.currentUser ?: throw Exception("No authenticated user")
                
                if (!userRepository.isUsernameUnique(username)) {
                    _authState.value = AuthState.Error("Username is already taken")
                    return@launch
                }

                val userProfile = User(
                    uid = currentUser.uid,
                    userName = username,
                    displayName = fullName,
                    email = currentUser.email ?: "",
                    bio = "I'm new to SpotShare!",
                    profilePicUrl = currentUser.photoUrl?.toString(),
                    website = null,
                    postsCount = 0,
                    followersCount = 0,
                    followingCount = 0,
                    isFollowing = false,
                    isPrivate = false,
                    savedPosts = emptyList(),
                    fcmToken = null
                )
                
                userRepository.updateProfile(userProfile).getOrThrow()
                _authState.value = AuthState.Success(currentUser.email ?: "")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Onboarding failed")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val email: String) : AuthState()
    data class RequiresOnboarding(val email: String) : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}
