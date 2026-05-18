package com.spotshare.presentation.screens.addspot

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.spotshare.domain.model.SpotCategory
import com.spotshare.presentation.components.CameraPreview
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun AddSpotScreen(
    onSpotAdded: () -> Unit,
    onCameraClick: () -> Unit,
    viewModel: AddSpotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()
    
    AddSpotContent(
        uiState = uiState,
        locationState = locationState,
        onSpotAdded = onSpotAdded,
        onCameraClick = onCameraClick,
        onFetchLocation = { viewModel.fetchCurrentLocation() },
        onAddSpot = { name, desc, cat, images, tags ->
            viewModel.addSpot(name, desc, cat, images, tags)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddSpotContent(
    uiState: AddSpotUiState,
    locationState: LocationState,
    onSpotAdded: () -> Unit,
    onCameraClick: () -> Unit,
    onFetchLocation: () -> Unit,
    onAddSpot: (String, String, SpotCategory, List<String>, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(SpotCategory.OTHER) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showCamera by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(setOf<String>()) }
    
    val scrollState = rememberScrollState()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = (selectedImages + uris).take(5)
    }

    LaunchedEffect(uiState) {
        if (uiState is AddSpotUiState.Success) {
            onSpotAdded()
        }
    }

    if (showCamera) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onImageCaptured = { uri ->
                selectedImages = (selectedImages + uri).take(5)
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Share a Spot") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Spot Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SpotCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Image Selection Section
                Text("Photos (${selectedImages.size}/5)", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { 
                            if (permissionState.allPermissionsGranted) {
                                galleryLauncher.launch("image/*")
                            } else {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Gallery")
                    }
                    Button(
                        onClick = { 
                            if (permissionState.allPermissionsGranted) {
                                onCameraClick()
                            } else {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Camera")
                    }
                }

                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(100.dp).fillMaxWidth()
                    ) {
                        items(selectedImages) { uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { selectedImages = selectedImages.filter { it != uri } },
                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                // Tag Input Section
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { 
                        if (it.endsWith(" ") || it.endsWith(",")) {
                            val tag = it.trim().removeSuffix(",").trim()
                            if (tag.isNotBlank()) tags = tags + tag
                            tagInput = ""
                        } else {
                            tagInput = it 
                        }
                    },
                    label = { Text("Add Tags (space or comma to add)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { tags = tags - tag },
                                label = { Text(tag) },
                                trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(18.dp)) }
                            )
                        }
                    }
                }

                Button(
                    onClick = { 
                        if (permissionState.allPermissionsGranted) {
                            onFetchLocation()
                        } else {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    if (locationState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondary)
                    } else {
                        Icon(Icons.Default.LocationOn, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (locationState.latitude != null) "Location Detected" else "Detect My Location")
                    }
                }

                if (locationState.address != null) {
                    Text(
                        text = "Address: ${locationState.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is AddSpotUiState.Loading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            onAddSpot(
                                name,
                                description,
                                category,
                                selectedImages.map { it.toString() },
                                tags.toList()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank() && description.isNotBlank() && selectedImages.isNotEmpty() && locationState.latitude != null
                    ) {
                        Text("Share Spot")
                    }
                }

                if (uiState is AddSpotUiState.Error) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun AddSpotScreenPreview() {
    SpotShareTheme {
        AddSpotContent(
            uiState = AddSpotUiState.Idle,
            locationState = LocationState(),
            onSpotAdded = {},
            onCameraClick = {},
            onFetchLocation = {},
            onAddSpot = { _, _, _, _, _ -> }
        )
    }
}
