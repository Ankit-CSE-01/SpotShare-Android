package com.spotshare.domain.repository

import com.spotshare.domain.model.Post
import com.spotshare.domain.model.StoryGroup
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(): Flow<List<Post>>
    fun getStories(): Flow<List<StoryGroup>>
    suspend fun refreshFeed(): Result<Unit>
    suspend fun likePost(postId: String): Result<Unit>
}
