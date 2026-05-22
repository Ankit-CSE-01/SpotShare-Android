package com.spotshare.presentation.screens.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.BuildConfig
import com.spotshare.data.remote.api.PexelsApiService
import com.spotshare.domain.model.Media
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Post
import com.spotshare.domain.model.StoryGroup
import com.spotshare.domain.repository.FeedRepository
import com.spotshare.domain.repository.PostRepository
import com.spotshare.domain.usecase.feed.GetFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val feedRepository: FeedRepository,
    private val postRepository: PostRepository,
    private val pexelsApi: PexelsApiService,
    private val auth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _postsList = MutableStateFlow<List<Post>>(emptyList())
    val postsList: StateFlow<List<Post>> = _postsList.asStateFlow()

    private val _stories = MutableStateFlow<List<StoryGroup>>(emptyList())
    val stories: StateFlow<List<StoryGroup>> = _stories.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    init {
        loadFeedFromPexels()
        loadStories()
    }

    fun loadFeedFromPexels(query: String = "travel destinations") {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            try {
                val response = pexelsApi.searchPhotos(
                    apiKey = BuildConfig.PEXELS_API_KEY,
                    query = query,
                    perPage = 30
                )
                val posts = response.photos.map { photo ->
                    Post(
                        id = photo.id.toString(),
                        userId = photo.photographerId.toString(),
                        userName = photo.photographer,
                        userProfilePic = null,
                        media = listOf(Media(photo.src.large, MediaType.IMAGE)),
                        caption = photo.alt.ifEmpty { "Discovering this amazing spot!" },
                        location = null,
                        locationName = "Hidden Gem",
                        likes = (100..1000).random(),
                        commentCount = (10..50).random(),
                        timestamp = System.currentTimeMillis() - (1000 * 60 * 60 * (1..24).random()),
                        isLiked = false,
                        isSaved = false,
                        tags = emptyList(),
                        rating = (3..5).random().toFloat()
                    )
                }
                _postsList.value = posts
                if (posts.isEmpty()) {
                    _error.value = "No photos found for '$query'"
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching Pexels photos", e)
                _error.value = "Failed to load feed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadStories() {
        feedRepository.getStories().onEach {
            _stories.value = it
        }.launchIn(viewModelScope)
    }

    fun refreshFeed() {
        loadFeedFromPexels()
    }

    fun likePost(postId: String) {
        _postsList.update { list ->
            list.map { if (it.id == postId) it.copy(isLiked = !it.isLiked, likes = if (it.isLiked) it.likes - 1 else it.likes + 1) else it }
        }
    }

    fun filterByCategory(category: String) {
        if (category == "All") loadFeedFromPexels()
        else loadFeedFromPexels(category)
    }

    fun savePost(postId: String) {
        _postsList.update { list ->
            list.map { if (it.id == postId) it.copy(isSaved = !it.isSaved) else it }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
