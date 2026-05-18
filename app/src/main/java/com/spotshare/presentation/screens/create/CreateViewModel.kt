package com.spotshare.presentation.screens.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.Location
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Post
import com.spotshare.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _selectedMedia = MutableStateFlow<List<SelectedMedia>>(emptyList())
    val selectedMedia = _selectedMedia.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    fun addMedia(uri: Uri, type: MediaType) {
        if (_selectedMedia.value.size < 5) {
            _selectedMedia.value += SelectedMedia(uri, type)
        }
    }

    fun removeMedia(uri: Uri) {
        _selectedMedia.value = _selectedMedia.value.filter { it.uri != uri }
    }

    fun uploadPost(caption: String, locationName: String?, lat: Double?, lng: Double?, rating: Float?) {
        viewModelScope.launch {
            _isUploading.value = true
            
            val postId = UUID.randomUUID().toString()
            val userId = auth.currentUser?.uid ?: ""
            val userName = auth.currentUser?.displayName ?: "User"
            
            val post = Post(
                id = postId,
                userId = userId,
                userName = userName,
                userProfilePic = auth.currentUser?.photoUrl?.toString(),
                media = emptyList(), // Will be populated after upload
                caption = caption,
                location = if (lat != null && lng != null) Location(lat, lng, locationName) else null,
                locationName = locationName,
                likes = 0,
                commentCount = 0,
                timestamp = System.currentTimeMillis(),
                isLiked = false,
                isSaved = false,
                tags = emptyList(),
                rating = rating
            )

            postRepository.enqueueUpload(post, _selectedMedia.value.map { it.uri.toString() })

            _isUploading.value = false
        }
    }
}

data class SelectedMedia(
    val uri: Uri,
    val type: MediaType
)
