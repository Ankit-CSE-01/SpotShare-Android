package com.spotshare.presentation.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.compose.clustering.Clustering
import com.spotshare.domain.model.Spot
import com.spotshare.domain.model.SpotCategory
import com.spotshare.presentation.components.SpotCard
import com.spotshare.presentation.theme.SpotShareTheme
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    onSpotClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    MapContent(
        uiState = uiState,
        onSpotClick = onSpotClick,
        onSearchArea = { lat, lng -> viewModel.loadSpots(lat, lng) },
        onSelectSpot = { viewModel.selectSpot(it) }
    )
}

@Composable
fun MapContent(
    uiState: MapUiState,
    onSpotClick: (String) -> Unit,
    onSearchArea: (Double, Double) -> Unit,
    onSelectSpot: (Spot?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            uiState.userLocation?.let { LatLng(it.first, it.second) } ?: LatLng(0.0, 0.0),
            if (uiState.userLocation != null) 15f else 2f
        )
    }

    val clusterItems = remember(uiState.spots) {
        uiState.spots.map { SpotClusterItem(it) }
    }

    var isMapMoving by remember { mutableStateOf(false) }

    LaunchedEffect(cameraPositionState.isMoving) {
        isMapMoving = cameraPositionState.isMoving
    }

    // Update camera if user location becomes available initially
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(it.first, it.second), 15f)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { onSelectSpot(null) },
            properties = MapProperties(isMyLocationEnabled = uiState.userLocation != null),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false)
        ) {
            Clustering(
                items = clusterItems,
                onClusterItemClick = { item ->
                    onSelectSpot(item.spot)
                    false
                },
                clusterContent = { cluster ->
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cluster.size.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                clusterItemContent = { item ->
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = item.spot.name,
                        tint = Color(getMarkerColor(item.spot.category).let { hue ->
                            android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
                        }),
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
        }

        // Search in this area button
        if (!isMapMoving && cameraPositionState.position.zoom > 10f) {
            Button(
                onClick = {
                    onSearchArea(
                        cameraPositionState.position.target.latitude,
                        cameraPositionState.position.target.longitude
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Search this area")
            }
        }

        // Floating controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (uiState.selectedSpot != null) 220.dp else 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    uiState.userLocation?.let {
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(it.first, it.second), 15f))
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }

        // Spot Preview Card
        uiState.selectedSpot?.let { spot ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                SpotCard(
                    spot = spot,
                    onClick = { onSpotClick(spot.id) }
                )
            }
        }
    }
}

private fun getMarkerColor(category: SpotCategory): Float {
    return when (category) {
        SpotCategory.FOOD -> BitmapDescriptorFactory.HUE_ORANGE
        SpotCategory.COFFEE -> 30f // Brownish-orange
        SpotCategory.NATURE -> BitmapDescriptorFactory.HUE_GREEN
        SpotCategory.ART -> BitmapDescriptorFactory.HUE_VIOLET
        SpotCategory.VIEWPOINT -> BitmapDescriptorFactory.HUE_AZURE
        SpotCategory.HISTORIC -> BitmapDescriptorFactory.HUE_YELLOW
        SpotCategory.OTHER -> BitmapDescriptorFactory.HUE_RED
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    SpotShareTheme {
        MapContent(
            uiState = MapUiState(
                spots = emptyList(),
                selectedSpot = null,
                userLocation = Pair(0.0, 0.0)
            ),
            onSpotClick = {},
            onSearchArea = { _, _ -> },
            onSelectSpot = {}
        )
    }
}
