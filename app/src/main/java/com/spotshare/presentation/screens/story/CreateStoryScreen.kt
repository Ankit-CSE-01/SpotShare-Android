package com.spotshare.presentation.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spotshare.presentation.theme.SpotShareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(
    onBackClick: () -> Unit,
    onStoryCreated: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add to Story") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Story creation UI (CameraX) goes here.")
            Spacer(Modifier.height(32.dp))
            Button(onClick = onStoryCreated, modifier = Modifier.fillMaxWidth()) {
                Text("Post Story")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateStoryScreenPreview() {
    SpotShareTheme {
        CreateStoryScreen(onBackClick = {}, onStoryCreated = {})
    }
}
