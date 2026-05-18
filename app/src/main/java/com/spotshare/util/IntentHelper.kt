package com.spotshare.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.spotshare.domain.model.Location
import com.spotshare.domain.model.Post

object IntentHelper {
    fun sharePost(context: Context, post: Post) {
        val shareText = "Check out this spot: ${post.caption}\nLocation: ${post.locationName ?: "Unknown"}\nShared from SpotShare"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun openInMaps(context: Context, location: Location) {
        val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${Uri.encode(location.name ?: "")}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        context.startActivity(mapIntent)
    }
}
