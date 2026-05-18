package com.spotshare.presentation.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spotshare.presentation.theme.SpotShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerScreen(
    onBackClick: () -> Unit,
    onMediaSelected: (List<Uri>) -> Unit
) {
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    // In a real app, we'd use ScopedStorageHelper to fetch from MediaStore
    // For now, let's use the standard system picker for simplicity in this demo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedUris = uris
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Media") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (selectedUris.isNotEmpty()) {
                        IconButton(onClick = { onMediaSelected(selectedUris) }) {
                            Icon(Icons.Default.Check, "Done")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedUris.isEmpty()) {
                Button(onClick = { launcher.launch("image/* video/*") }) {
                    Text("Open Gallery")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(selectedUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MediaPickerScreenPreview() {
    SpotShareTheme {
        MediaPickerScreen(onBackClick = {}, onMediaSelected = {})
    }
}
