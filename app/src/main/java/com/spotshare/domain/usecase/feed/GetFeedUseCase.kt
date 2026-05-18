package com.spotshare.domain.usecase.feed

import com.google.firebase.auth.FirebaseAuth
import com.spotshare.domain.model.Post
import com.spotshare.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val repository: FeedRepository,
    private val algorithm: FeedAlgorithm,
    private val auth: FirebaseAuth
) {
    operator fun invoke(): Flow<List<Post>> {
        val userId = auth.currentUser?.uid ?: ""
        return repository.getFeed().map { posts ->
            algorithm.rankPosts(posts, userId)
        }
    }
}
