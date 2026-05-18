package com.spotshare.presentation.screens.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.spotshare.domain.model.Post
import com.spotshare.presentation.components.common.CustomRatingBar
import com.spotshare.presentation.components.media.MediaCarousel

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onProfileClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onLocationClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onPostClick(post.id) }) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable { onProfileClick(post.userId) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.userProfilePic ?: "https://via.placeholder.com/32",
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.userName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                if (post.locationName != null) {
                    Text(
                        text = post.locationName,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { onLocationClick() }
                    )
                }
            }
            IconButton(onClick = { /* More options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        
        // Media Carousel
        MediaCarousel(
            mediaList = post.media,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        
        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLike) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) Color.Red else LocalContentColor.current,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onComment) {
                Icon(Icons.AutoMirrored.Outlined.Comment, contentDescription = "Comment", modifier = Modifier.size(26.dp))
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Outlined.Share, contentDescription = "Share", modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onSave) {
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", modifier = Modifier.size(28.dp))
            }
        }
        
        // Content
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = "${post.likes} likes",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(post.userName)
                        append(" ")
                    }
                    append(post.caption)
                },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            if (post.rating != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Rating: ", style = MaterialTheme.typography.labelSmall)
                    CustomRatingBar(
                        rating = post.rating,
                        starSize = 14.dp,
                        enabled = false
                    )
                }
            }
            
            if (post.commentCount > 0) {
                Text(
                    text = "View all ${post.commentCount} comments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { onComment() }
                )
            }
            
            Text(
                text = "2 hours ago", // Placeholder for actual time calculation
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
