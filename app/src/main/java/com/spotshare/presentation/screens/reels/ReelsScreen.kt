package com.spotshare.presentation.screens.reels

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotshare.domain.model.Reel
import com.spotshare.presentation.theme.SpotShareTheme

/**
 * Full-screen Reels experience using Media3 ExoPlayer.
 * Supports vertical scrolling and interactive action overlays.
 */
@Composable
fun ReelsScreen(
    onBackClick: () -> Unit,
    onCameraClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onLocationClick: (com.spotshare.domain.model.Location) -> Unit,
    viewModel: ReelsViewModel = hiltViewModel()
) {
    val reels by viewModel.reels.collectAsState()
    ReelsContent(
        reels = reels,
        onBackClick = onBackClick,
        onCameraClick = onCameraClick,
        onLikeClick = { viewModel.likeReel(it) },
        onProfileClick = onProfileClick,
        onLocationClick = onLocationClick
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
    onLocationClick: (com.spotshare.domain.model.Location) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            ReelPlayer(
                reel = reel,
                isActive = page == pagerState.currentPage,
                onLike = { onLikeClick(reel.id) },
                onComment = { /* Show comments */ },
                onShare = { /* Share */ },
                onProfileClick = onProfileClick,
                onLocationClick = onLocationClick,
                onMoreClick = { /* More options */ }
            )
        }

        // Transparent Top Bar
        TopAppBar(
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Reels",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            actions = {
                IconButton(onClick = onCameraClick) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview
@Composable
fun ReelsScreenPreview() {
    SpotShareTheme {
        ReelsContent(
            reels = listOf(
                Reel(
                    id = "1",
                    userId = "u1",
                    userName = "john_doe",
                    userProfilePic = null,
                    videoUrl = "",
                    thumbnailUrl = "",
                    caption = "Cool sunset!",
                    location = null,
                    locationName = "Beach",
                    audioName = "Relaxing Music",
                    likes = 120,
                    commentCount = 10,
                    shareCount = 5,
                    viewCount = 1000,
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
            onLocationClick = {}
        )
    }
}
