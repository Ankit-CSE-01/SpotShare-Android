package com.spotshare.presentation.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit,
    onCameraClick: () -> Unit,
    onOpenGallery: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel()
) {
    val selectedMedia by viewModel.selectedMedia.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    CreatePostContent(
        selectedMedia = selectedMedia,
        isUploading = isUploading,
        onBackClick = onBackClick,
        onPostCreated = onPostCreated,
        onCameraClick = onCameraClick,
        onOpenGallery = onOpenGallery,
        onRemoveMedia = { viewModel.removeMedia(it) },
        onUploadPost = { caption, rating -> 
            viewModel.uploadPost(caption, null, null, null, rating)
            onPostCreated()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostContent(
    selectedMedia: List<SelectedMedia>,
    isUploading: Boolean,
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit,
    onCameraClick: () -> Unit,
    onOpenGallery: () -> Unit,
    onRemoveMedia: (android.net.Uri) -> Unit,
    onUploadPost: (String, Float) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 16.dp))
                    } else {
                        TextButton(
                            onClick = { onUploadPost(caption, rating) },
                            enabled = selectedMedia.isNotEmpty() && caption.isNotBlank()
                        ) {
                            Text("Share", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Media Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenGallery,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
            }

            if (selectedMedia.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(120.dp).fillMaxWidth()
                ) {
                    items(selectedMedia) { media ->
                        Box {
                            AsyncImage(
                                model = media.uri,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onRemoveMedia(media.uri) },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close, 
                                    null, 
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Write a caption...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Column {
                Text("Rate this place", style = MaterialTheme.typography.labelMedium)
                com.spotshare.presentation.components.common.CustomRatingBar(
                    rating = rating,
                    onRatingChange = { rating = it }
                )
            }
            
            ListItem(
                headlineContent = { Text("Add Location") },
                leadingContent = { Icon(Icons.Default.CameraAlt, null) }, // Placeholder for Location icon
                trailingContent = { Text("Select >", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { /* Select location */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    SpotShareTheme {
        CreatePostContent(
            selectedMedia = emptyList(),
            isUploading = false,
            onBackClick = {},
            onPostCreated = {},
            onCameraClick = {},
            onOpenGallery = {},
            onRemoveMedia = {},
            onUploadPost = { _, _ -> }
        )
    }
}
