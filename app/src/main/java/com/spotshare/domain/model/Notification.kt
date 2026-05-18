package com.spotshare.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val fromUserId: String,
    val fromUserName: String,
    val fromUserProfilePic: String?,
    val postId: String?,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, MESSAGE, STORY_VIEW
}
