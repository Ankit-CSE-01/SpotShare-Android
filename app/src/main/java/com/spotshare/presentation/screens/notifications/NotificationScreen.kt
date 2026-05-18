package com.spotshare.presentation.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.Notification
import com.spotshare.domain.model.NotificationType
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    NotificationContent(
        notifications = notifications,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationContent(
    notifications: List<Notification>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(notifications) { notification ->
                NotificationItem(notification = notification)
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    ListItem(
        leadingContent = {
            AsyncImage(
                model = notification.fromUserProfilePic ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        },
        headlineContent = {
            Text(
                text = buildString {
                    append(notification.fromUserName)
                    append(" ")
                    append(notification.message)
                },
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = {
            Text(text = "2h ago", style = MaterialTheme.typography.labelSmall)
        },
        trailingContent = {
            notification.postId?.let {
                AsyncImage(
                    model = "https://via.placeholder.com/40",
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    SpotShareTheme {
        NotificationContent(
            notifications = listOf(
                Notification(
                    id = "1",
                    type = NotificationType.LIKE,
                    fromUserId = "u1",
                    fromUserName = "john_doe",
                    fromUserProfilePic = null,
                    postId = "p1",
                    message = "liked your post",
                    timestamp = 0,
                    isRead = false
                )
            ),
            onBackClick = {}
        )
    }
}
