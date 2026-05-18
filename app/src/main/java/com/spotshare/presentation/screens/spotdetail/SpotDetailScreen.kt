package com.spotshare.presentation.screens.spotdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.Review
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun SpotDetailScreen(
    onBackClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    viewModel: SpotDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    
    SpotDetailContent(
        uiState = uiState,
        reviews = reviews,
        isSaved = isSaved,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onToggleSave = { viewModel.toggleSave() },
        onSubmitReview = { rating, comment -> viewModel.submitReview(rating, comment) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotDetailContent(
    uiState: SpotDetailUiState,
    reviews: List<Review>,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onToggleSave: () -> Unit,
    onSubmitReview: (Int, String) -> Unit
) {
    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spot Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Spot",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = {
                        val spotName = if (uiState is SpotDetailUiState.Success) uiState.spot.name else ""
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out this spot on SpotShare: $spotName")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showReviewDialog = true }) {
                Icon(Icons.Default.Star, contentDescription = "Add Review")
            }
        }
    ) { padding ->
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = { Text("Write a Review") },
                text = {
                    Column {
                        Text("Rating: $rating/5")
                        Slider(
                            value = rating.toFloat(),
                            onValueChange = { rating = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Your thoughts...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onSubmitReview(rating, comment)
                        showReviewDialog = false
                        comment = ""
                        rating = 5
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReviewDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        when (val state = uiState) {
            is SpotDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SpotDetailUiState.Success -> {
                val spot = state.spot
                val pagerState = rememberPagerState(pageCount = { spot.imageUrls.size })

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = spot.imageUrls[page],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            // Image Indicators
                            if (spot.imageUrls.size > 1) {
                                Row(
                                    Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(spot.imageUrls.size) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) Color.White else Color.Gray.copy(alpha = 0.5f)
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .size(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = spot.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = spot.category.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300))
                                    Text(
                                        text = String.format("%.1f", spot.rating),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                            
                            if (spot.address != null) {
                                Text(
                                    text = spot.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "About this spot",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = spot.description,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Shared by ${spot.createdBy}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onProfileClick(spot.createdBy) }
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${spot.latitude},${spot.longitude}?q=${Uri.encode(spot.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Get Directions")
                            }
                        }
                    }
                    item {
                        Text(
                            text = "Reviews (${reviews.size})",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (reviews.isEmpty()) {
                        item {
                            Text(
                                text = "No reviews yet. Be the first to share your experience!",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(reviews) { review ->
                            ListItem(
                                headlineContent = { Text(review.userName, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(review.comment) },
                                trailingContent = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${review.rating}")
                                        Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = Color(0xFFFFB300))
                                    }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
            is SpotDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpotDetailScreenPreview() {
    SpotShareTheme {
        SpotDetailContent(
            uiState = SpotDetailUiState.Success(
                Spot(
                    id = "1",
                    name = "Central Park",
                    description = "A beautiful park.",
                    category = SpotCategory.NATURE,
                    latitude = 0.0,
                    longitude = 0.0,
                    imageUrls = listOf(""),
                    rating = 4.5,
                    reviewCount = 10,
                    createdBy = "user1",
                    createdAt = 0,
                    address = "NYC",
                    tags = emptyList()
                )
            ),
            reviews = emptyList(),
            isSaved = false,
            onBackClick = {},
            onProfileClick = {},
            onToggleSave = {},
            onSubmitReview = { _, _ -> }
        )
    }
}
