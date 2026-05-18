package com.spotshare.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val mediaUrl: String?,
    val mediaType: MediaType?,
    val timestamp: Long,
    val isRead: Boolean
)

data class Chat(
    val id: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserProfilePic: String?,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)
