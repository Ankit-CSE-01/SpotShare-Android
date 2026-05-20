package com.spotshare.presentation.screens.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.User
import com.spotshare.presentation.theme.SpotShareTheme
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Comprehensive Profile Screen.
 * Features stats, bio, highlights, and 4-tab content pager.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: (String) -> Unit,
    onFollowersClick: (String) -> Unit,
    onFollowingClick: (String) -> Unit,
    onSpotClick: (String) -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val createdSpots by viewModel.createdSpots.collectAsState()
    val navigateToChat by viewModel.navigateToChat.collectAsState()
    val isOwnProfile = viewModel.isOwnProfile
    
    LaunchedEffect(navigateToChat) {
        navigateToChat?.let { chatId ->
            onNavigateToChat(chatId)
            viewModel.onChatNavigated()
        }
    }

    ProfileContent(
        user = user,
        createdSpots = createdSpots,
        isOwnProfile = isOwnProfile,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onSettingsClick = onSettingsClick,
        onEditProfileClick = onEditProfileClick,
        onShareProfileClick = { onShareProfileClick(user?.uid ?: "") },
        onFollowersClick = { onFollowersClick(user?.uid ?: "") },
        onFollowingClick = { onFollowingClick(user?.uid ?: "") },
        onSpotClick = onSpotClick,
        onMessageClick = { viewModel.startChat() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User?,
    createdSpots: List<Spot>,
    isOwnProfile: Boolean,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onSpotClick: (String) -> Unit,
    onMessageClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = user?.userName ?: "Profile", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    if (!isOwnProfile) {
                        IconButton(onClick = { /* Back handled by NavHost */ }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                },
                actions = {
                    if (isOwnProfile) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 16.dp) // Added bottom padding for better scroll end
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Image + Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user?.profilePicUrl ?: "https://via.placeholder.com/150",
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clickable { /* View full size */ },
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Posts", value = user?.postsCount?.toString() ?: "0")
                    StatItem(label = "Followers", value = formatLargeNumber(user?.followersCount ?: 0), onClick = onFollowersClick)
                    StatItem(label = "Following", value = user?.followingCount?.toString() ?: "0", onClick = onFollowingClick)
                }
            }

            // Name + Bio + Location
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = user?.displayName ?: "Explorer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (user?.bio != null) {
                    Text(text = user.bio, fontSize = 14.sp, lineHeight = 18.sp)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Place, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(2.dp))
                    Text(text = "New York, USA", color = Color.Gray, fontSize = 13.sp)
                }

                if (user?.website != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.Link, null, modifier = Modifier.size(14.dp), tint = Color(0xFF00376B))
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = user.website,
                            color = Color(0xFF00376B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { /* Open link */ }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isOwnProfile) {
                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFEFEF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onShareProfileClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFEFEF),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text("Share Profile", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = { /* Follow */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Follow", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onMessageClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFEFEF),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Message", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Story Highlights
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(listOf("NYC", "Food", "Trip", "Work")) { title ->
                    HighlightItem(title = title, imageUrl = null)
                }
                if (isOwnProfile) {
                    item {
                        HighlightItem(title = "New", imageUrl = null, isNew = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = Color.Black,
                divider = { HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray) },
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = Color.Black,
                            height = 1.5.dp
                        )
                    }
                }
            ) {
                val icons = listOf(Icons.Default.GridOn, Icons.Default.PlayArrow, Icons.Default.PersonPin, Icons.Default.Place)
                icons.forEachIndexed { index, icon ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        icon = { Icon(icon, null, modifier = Modifier.size(24.dp), tint = if (pagerState.currentPage == index) Color.Black else Color.Gray) }
                    )
                }
            }

            // Pager Content
            Box(modifier = Modifier.height(600.dp)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> PostGrid(spots = createdSpots, onSpotClick = onSpotClick)
                        1 -> PlaceholderTab("Reels")
                        2 -> PlaceholderTab("Tagged")
                        3 -> PlaceholderTab("Map")
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightItem(title: String, imageUrl: String?, isNew: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(1.dp, Color.LightGray, CircleShape)
                .padding(4.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = Color(0xFFFAFAFA)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isNew) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(28.dp))
                    } else {
                        AsyncImage(
                            model = imageUrl ?: "https://via.placeholder.com/150",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(text = title, fontSize = 12.sp)
    }
}

@Composable
fun PostGrid(spots: List<Spot>, onSpotClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(spots) { spot ->
            Box(modifier = Modifier.aspectRatio(1f)) {
                AsyncImage(
                    model = spot.imageUrls.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onSpotClick(spot.id) },
                    contentScale = ContentScale.Crop
                )
                // Simulated Like/Video count overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, null, modifier = Modifier.size(12.dp), tint = Color.White)
                    Spacer(Modifier.width(2.dp))
                    Text("234", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlaceholderTab(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.Gray)
    }
}

@Composable
fun StatItem(label: String, value: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

private fun formatLargeNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SpotShareTheme {
        ProfileContent(
            user = User(
                uid = "1",
                userName = "john_doe",
                displayName = "John Doe",
                email = "john@example.com",
                bio = "Travel & Food Enthusiast 🌍🍕",
                profilePicUrl = null,
                website = "website.com",
                postsCount = 123,
                followersCount = 456000,
                followingCount = 789,
                isFollowing = false,
                isPrivate = false,
                savedPosts = emptyList(),
                fcmToken = null
            ),
            createdSpots = emptyList(),
            isOwnProfile = true,
            onLogout = {},
            onSettingsClick = {},
            onEditProfileClick = {},
            onShareProfileClick = {},
            onFollowersClick = {},
            onFollowingClick = {},
            onSpotClick = {},
            onMessageClick = {}
        )
    }
}
