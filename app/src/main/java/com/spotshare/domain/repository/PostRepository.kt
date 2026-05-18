package com.spotshare.domain.repository

import androidx.paging.PagingData
import com.spotshare.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPostsPagingFlow(): Flow<PagingData<Post>>
    suspend fun uploadPost(post: Post, mediaUris: List<String>): Result<Unit>
    fun enqueueUpload(post: Post, mediaUris: List<String>)
}
