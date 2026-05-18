package com.spotshare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val userProfilePic: String?,
    val text: String,
    val likes: Int,
    val isLiked: Boolean,
    val timestamp: Long,
    val hasReplies: Boolean
)
