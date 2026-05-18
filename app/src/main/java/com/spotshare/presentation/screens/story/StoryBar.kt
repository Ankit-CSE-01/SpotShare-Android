package com.spotshare.presentation.screens.story

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.spotshare.domain.model.StoryGroup

@Composable
fun StoryBar(
    stories: List<StoryGroup>,
    onStoryClick: (String) -> Unit,
    onAddStoryClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Your story
        item {
            StoryCircle(
                imageUrl = null,
                userName = "Your Story",
                hasUnviewed = false,
                isYourStory = true,
                onClick = onAddStoryClick
            )
        }
        
        // Others' stories
        items(stories, key = { it.userId }) { storyGroup ->
            StoryCircle(
                imageUrl = storyGroup.userProfilePic,
                userName = storyGroup.userName,
                hasUnviewed = storyGroup.hasUnviewed,
                onClick = { onStoryClick(storyGroup.userId) }
            )
        }
    }
}

@Composable
fun StoryCircle(
    imageUrl: String?,
    userName: String,
    hasUnviewed: Boolean,
    isYourStory: Boolean = false,
    onClick: () -> Unit
) {
    val instagramGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .size(72.dp) // Increased size for better touch target and visibility
                .then(
                    if (hasUnviewed) {
                        Modifier.border(2.5.dp, instagramGradient, CircleShape)
                    } else if (!isYourStory) {
                        Modifier.border(1.dp, Color.LightGray, CircleShape)
                    } else {
                        Modifier
                    }
                )
                .padding(if (hasUnviewed) 4.5.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFFFAFAFA)),
                contentScale = ContentScale.Crop
            )
            
            if (isYourStory) {
                Surface(
                    modifier = Modifier
                        .size(24.dp) // Slightly larger add button
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = if (isYourStory) "Your Story" else userName,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 12.sp, // Slightly larger font
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(72.dp)
        )
    }
}
