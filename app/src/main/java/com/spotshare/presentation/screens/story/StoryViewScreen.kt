package com.spotshare.presentation.screens.story

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.MediaType
import com.spotshare.domain.model.Story
import com.spotshare.domain.model.StoryGroup
import com.spotshare.presentation.components.media.VideoPlayer
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun StoryViewScreen(
    userId: String,
    onClose: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val storyGroups by viewModel.storyGroups.collectAsState()
    val initialPage = storyGroups.indexOfFirst { it.userId == userId }.coerceAtLeast(0)
    
    val verticalPagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { storyGroups.size }
    )
    
    VerticalPager(
        state = verticalPagerState,
        modifier = Modifier.fillMaxSize()
    ) { groupIndex ->
        val storyGroup = storyGroups[groupIndex]
        StoryGroupContent(
            storyGroup = storyGroup,
            onClose = onClose,
            onGroupFinished = {
                // Logic to go to next group or close
            }
        )
    }
}

@Composable
fun StoryGroupContent(
    storyGroup: StoryGroup,
    onClose: () -> Unit,
    onGroupFinished: () -> Unit
) {
    val horizontalPagerState = rememberPagerState(pageCount = { storyGroup.stories.size })
    
    // Auto-advance logic
    LaunchedEffect(horizontalPagerState.currentPage) {
        val duration = storyGroup.stories[horizontalPagerState.currentPage].duration
        kotlinx.coroutines.delay(duration)
        if (horizontalPagerState.currentPage < storyGroup.stories.size - 1) {
            horizontalPagerState.animateScrollToPage(horizontalPagerState.currentPage + 1)
        } else {
            onGroupFinished()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        HorizontalPager(state = horizontalPagerState) { page ->
            val story = storyGroup.stories[page]
            when (story.mediaType) {
                MediaType.IMAGE -> {
                    AsyncImage(
                        model = story.mediaUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                MediaType.VIDEO -> {
                    VideoPlayer(videoUrl = story.mediaUrl, modifier = Modifier.fillMaxSize())
                }
            }
        }
        
        // Progress bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            storyGroup.stories.forEachIndexed { index, _ ->
                val progress = remember { Animatable(0f) }
                
                LaunchedEffect(horizontalPagerState.currentPage) {
                    if (index < horizontalPagerState.currentPage) {
                        progress.snapTo(1f)
                    } else if (index == horizontalPagerState.currentPage) {
                        progress.snapTo(0f)
                        progress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = storyGroup.stories[index].duration.toInt(),
                                easing = LinearEasing
                            )
                        )
                    } else {
                        progress.snapTo(0f)
                    }
                }

                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier.weight(1f).height(2.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
        }
        
        // User Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = storyGroup.userProfilePic,
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(storyGroup.userName, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun StoryGroupContentPreview() {
    SpotShareTheme {
        StoryGroupContent(
            storyGroup = StoryGroup(
                userId = "1",
                userName = "john_doe",
                userProfilePic = null,
                stories = listOf(
                    Story(
                        id = "s1",
                        userId = "1",
                        userName = "john_doe",
                        userProfilePic = null,
                        mediaUrl = "",
                        mediaType = MediaType.IMAGE,
                        duration = 5000,
                        location = null,
                        timestamp = 0,
                        expiresAt = 0,
                        views = emptyList(),
                        isViewed = false
                    )
                ),
                hasUnviewed = true
            ),
            onClose = {},
            onGroupFinished = {}
        )
    }
}
