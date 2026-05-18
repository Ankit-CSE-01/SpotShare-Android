package com.spotshare.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.spotshare.presentation.theme.SpotShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialListScreen(
    username: String,
    initialTab: Int = 0,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = username, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color.Black
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("456K Followers", fontSize = 14.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("789 Following", fontSize = 14.sp) }
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFEFEFEF),
                    focusedContainerColor = Color(0xFFEFEFEF),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listOf("All", "Contacts", "Least Interacted", "Most Shown", "Verified")) { category ->
                    FilterChip(
                        selected = category == "All",
                        onClick = { },
                        label = { Text(category) }
                    )
                }
            }

            // List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = if (selectedTab == 0) "Suggested" else "Categories",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(10) { index ->
                    SocialUserItem(
                        username = "user_$index",
                        name = "User Name $index",
                        isFollowing = index % 3 == 0,
                        isFollower = selectedTab == 0,
                        onClick = { onUserClick("u$index") }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialUserItem(
    username: String,
    name: String,
    isFollowing: Boolean,
    isFollower: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            AsyncImage(
                model = "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(54.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        },
        headlineContent = {
            Text(text = username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        },
        supportingContent = {
            Text(text = name, color = Color.Gray, fontSize = 14.sp)
        },
        trailingContent = {
            val buttonText = when {
                isFollowing -> "Following"
                isFollower -> "Follow Back"
                else -> "Follow"
            }
            val isPrimary = buttonText != "Following"

            Button(
                onClick = { },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = if (isPrimary) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEFEFEF),
                        contentColor = Color.Black
                    )
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(text = buttonText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SocialListScreenPreview() {
    SpotShareTheme {
        SocialListScreen(
            username = "john_doe",
            onBackClick = {},
            onUserClick = {}
        )
    }
}
