package com.spotshare.presentation.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotshare.domain.model.Post
import com.spotshare.domain.model.StoryGroup
import com.spotshare.presentation.screens.feed.components.PostCard
import com.spotshare.presentation.screens.story.StoryBar
import com.spotshare.presentation.theme.SpotShareTheme
import com.spotshare.util.IntentHelper
import kotlinx.coroutines.launch

/**
 * Main Feed Screen representing the Home tab.
 * Displays Instagram-style posts and stories using Pexels API.
 */
@Composable
fun FeedScreen(
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onStoryClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val posts by viewModel.postsList.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    
    FeedContent(
        posts = posts,
        stories = stories,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        onNotificationsClick = onNotificationsClick,
        onMessagesClick = onMessagesClick,
        onAddStoryClick = onAddStoryClick,
        onStoryClick = onStoryClick,
        onPostClick = onPostClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick,
        onLogout = {
            viewModel.logout()
            onLogout()
        },
        onLikeClick = { viewModel.likePost(it) },
        onSaveClick = { viewModel.savePost(it) },
        onFilterCategory = { viewModel.filterByCategory(it) },
        onRefresh = { viewModel.refreshFeed() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    posts: List<Post>,
    stories: List<StoryGroup>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onStoryClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onProfileClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    onLikeClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    onFilterCategory: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { 
                        scope.launch { 
                            drawerState.close()
                            onProfileClick("current_user") 
                        } 
                    },
                    icon = { Icon(Icons.Default.Person, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { 
                        scope.launch { 
                            drawerState.close()
                            onSettingsClick() 
                        } 
                    },
                    icon = { Icon(Icons.Default.Settings, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { 
                        scope.launch { 
                            drawerState.close()
                            onLogout()
                        } 
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "SpotShare", 
                            fontSize = 26.sp, 
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier.padding(start = 4.dp)
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Outlined.FavoriteBorder, "Activity", modifier = Modifier.size(26.dp))
                        }
                        IconButton(onClick = onMessagesClick) {
                            Icon(Icons.AutoMirrored.Filled.Message, "Messages", modifier = Modifier.size(24.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    StoryBar(
                        stories = stories,
                        onStoryClick = onStoryClick,
                        onAddStoryClick = onAddStoryClick
                    )
                }
                
                item {
                    CategoryFilterChips(onCategorySelected = onFilterCategory)
                }
                
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLike = { onLikeClick(post.id) },
                        onComment = { onPostClick(post.id) },
                        onShare = { IntentHelper.sharePost(context, post) },
                        onSave = { onSaveClick(post.id) },
                        onProfileClick = onProfileClick,
                        onPostClick = onPostClick,
                        onLocationClick = { post.location?.let { loc -> IntentHelper.openInMaps(context, loc) } }
                    )
                }
                
                if (posts.isEmpty() && !isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No content available.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = onRefresh) {
                                    Text("Retry")
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Note: Ensure Firestore is enabled in Firebase Console and your internet connection is active.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChips(onCategorySelected: (String) -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Food", "Coffee", "Nature", "Art", "Viewpoint", "Historic")
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = {
                    selectedCategory = category
                    onCategorySelected(category)
                },
                label = { 
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge
                    ) 
                },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFEFEFEF),
                    labelColor = Color.Black
                ),
                border = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    SpotShareTheme {
        FeedContent(
            posts = emptyList(),
            stories = emptyList(),
            isRefreshing = false,
            isLoadingMore = false,
            onNotificationsClick = {},
            onMessagesClick = {},
            onAddStoryClick = {},
            onStoryClick = {},
            onPostClick = {},
            onProfileClick = {},
            onSettingsClick = {},
            onLogout = {},
            onLikeClick = {},
            onSaveClick = {},
            onFilterCategory = {},
            onRefresh = {}
        )
    }
}
