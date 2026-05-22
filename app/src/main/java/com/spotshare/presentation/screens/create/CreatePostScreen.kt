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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    CreatePostContent(
        selectedMedia = selectedMedia,
        isUploading = isUploading,
        selectedLocation = selectedLocation,
        onBackClick = onBackClick,
        onPostCreated = onPostCreated,
        onCameraClick = onCameraClick,
        onOpenGallery = onOpenGallery,
        onRemoveMedia = { viewModel.removeMedia(it) },
        onLocationSelect = { viewModel.setLocation(it) },
        onUploadPost = { caption, rating -> 
            viewModel.uploadPost(caption, rating)
            onPostCreated()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostContent(
    selectedMedia: List<SelectedMedia>,
    isUploading: Boolean,
    selectedLocation: String?,
    onBackClick: () -> Unit,
    onPostCreated: () -> Unit,
    onCameraClick: () -> Unit,
    onOpenGallery: () -> Unit,
    onRemoveMedia: (android.net.Uri) -> Unit,
    onLocationSelect: (String) -> Unit,
    onUploadPost: (String, Float) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5f) }
    var showLocationPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.Bold) },
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
                            Text("Share", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFEFEF), contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFEFEF), contentColor = Color.Black)
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
                                    tint = Color.White,
                                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFF5F5F5), MaterialTheme.shapes.medium)
                        .clickable { onOpenGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select photos or videos", color = Color.Gray)
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Write a caption...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Column {
                Text("Rate this place", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                com.spotshare.presentation.components.common.CustomRatingBar(
                    rating = rating,
                    onRatingChange = { rating = it }
                )
            }
            
            ListItem(
                headlineContent = { Text("Location") },
                supportingContent = { Text(selectedLocation ?: "Add location", color = if (selectedLocation != null) Color.Black else Color.Gray) },
                leadingContent = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) },
                trailingContent = { Text("Select >", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { showLocationPicker = true }
            )
        }

        if (showLocationPicker) {
            LocationPickerDialog(
                onDismiss = { showLocationPicker = false },
                onLocationSelect = { 
                    onLocationSelect(it)
                    showLocationPicker = false
                }
            )
        }
    }
}

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelect: (String) -> Unit
) {
    val locations = listOf("Central Park", "Times Square", "Brooklyn Bridge", "SoHo", "Grand Central")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            Column {
                locations.forEach { location ->
                    ListItem(
                        headlineContent = { Text(location) },
                        modifier = Modifier.clickable { onLocationSelect(location) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    SpotShareTheme {
        CreatePostContent(
            selectedMedia = emptyList(),
            isUploading = false,
            selectedLocation = null,
            onBackClick = {},
            onPostCreated = {},
            onCameraClick = {},
            onOpenGallery = {},
            onRemoveMedia = {},
            onLocationSelect = {},
            onUploadPost = { _, _ -> }
        )
    }
}
