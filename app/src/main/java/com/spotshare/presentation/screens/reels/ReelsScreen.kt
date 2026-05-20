package com.spotshare.presentation.screens.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotshare.domain.model.Reel
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun ReelsScreen(
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onLocationClick: (String) -> Unit,
    onMessageClick: (String) -> Unit,
    viewModel: ReelsViewModel = hiltViewModel()
) {
    val reels by viewModel.reels.collectAsState()

    ReelsContent(
        reels = reels,
        onBackClick = onBackClick,
        onCameraClick = onCameraClick,
        onProfileClick = onProfileClick,
        onLikeClick = { viewModel.likeReel(it) },
        onLocationClick = onLocationClick,
        onMessageClick = onMessageClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsContent(
    reels: List<Reel>,
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onLocationClick: (String) -> Unit,
    onMessageClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            if (reels.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val reel = reels[page]
                    ReelPlayer(
                        reel = reel,
                        isActive = pagerState.currentPage == page,
                        onLike = { onLikeClick(reel.id) },
                        onComment = { /* Handle comment */ },
                        onShare = { /* Handle share */ },
                        onProfileClick = { onProfileClick(reel.userId) },
                        onLocationClick = { /* Handle location */ },
                        onMoreClick = { /* Handle more */ }
                    )
                }
            }

            // Top Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    "Reels",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                IconButton(onClick = onCameraClick) {
                    Icon(Icons.Default.CameraAlt, "Camera", tint = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReelsScreenPreview() {
    SpotShareTheme {
        ReelsContent(
            reels = listOf(
                Reel(
                    id = "1",
                    userId = "u1",
                    userName = "jane_smith",
                    userProfilePic = null,
                    videoUrl = "",
                    thumbnailUrl = "",
                    caption = "Beautiful sunset!",
                    location = null,
                    locationName = "Malibu, CA",
                    audioName = "Original Audio",
                    likes = 1200,
                    commentCount = 45,
                    shareCount = 12,
                    viewCount = 5000,
                    timestamp = 0,
                    isLiked = false,
                    isSaved = false,
                    tags = emptyList()
                )
            ),
            onBackClick = {},
            onCameraClick = {},
            onLikeClick = {},
            onProfileClick = {},
            onLocationClick = {},
            onMessageClick = {}
        )
    }
}
