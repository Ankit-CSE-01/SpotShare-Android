package com.spotshare.presentation.screens.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.domain.model.StoryGroup
import com.spotshare.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {

    private val _storyGroups = MutableStateFlow<List<StoryGroup>>(emptyList())
    val storyGroups: StateFlow<List<StoryGroup>> = _storyGroups.asStateFlow()

    init {
        feedRepository.getStories().onEach {
            _storyGroups.value = it
        }.launchIn(viewModelScope)
    }

    fun markAsViewed(storyId: String) {
        viewModelScope.launch {
            // Logic to mark story as viewed in repository
        }
    }
}
