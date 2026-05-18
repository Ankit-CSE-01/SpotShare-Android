package com.spotshare.presentation.screens.chat

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.Chat
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun ChatListScreen(
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()

    ChatListContent(
        chats = chats,
        onBackClick = onBackClick,
        onChatClick = onChatClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListContent(
    chats: List<Chat>,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
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
            items(chats) { chat ->
                ChatItem(chat = chat, onClick = { onChatClick(chat.id) })
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            AsyncImage(
                model = chat.otherUserProfilePic ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
        },
        headlineContent = {
            Text(text = chat.otherUserName, fontWeight = FontWeight.Bold)
        },
        supportingContent = {
            Text(
                text = chat.lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = {
            if (chat.unreadCount > 0) {
                Badge { Text(chat.unreadCount.toString()) }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    SpotShareTheme {
        ChatListContent(
            chats = listOf(
                Chat(
                    id = "1",
                    otherUserId = "u1",
                    otherUserName = "jane_smith",
                    otherUserProfilePic = null,
                    lastMessage = "Hey there!",
                    lastMessageTime = 0,
                    unreadCount = 2
                )
            ),
            onBackClick = {},
            onChatClick = {}
        )
    }
}
