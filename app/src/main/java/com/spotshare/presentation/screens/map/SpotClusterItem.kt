package com.spotshare.presentation.screens.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.spotshare.domain.model.Spot

data class SpotClusterItem(
    val spot: Spot
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(spot.latitude, spot.longitude)
    override fun getTitle(): String = spot.name
    override fun getSnippet(): String = spot.description
    override fun getZIndex(): Float? = null
}
