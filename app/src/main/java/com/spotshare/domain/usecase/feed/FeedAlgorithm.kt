package com.spotshare.domain.usecase.feed

import com.spotshare.domain.model.Post
import javax.inject.Inject

class FeedAlgorithm @Inject constructor() {
    fun rankPosts(posts: List<Post>, userId: String): List<Post> {
        return posts.sortedByDescending { post ->
            calculateScore(post, userId)
        }
    }

    private fun calculateScore(post: Post, userId: String): Float {
        var score = 0f
        
        // Recency (within last 24h)
        val hoursSincePost = (System.currentTimeMillis() - post.timestamp) / (1000 * 60 * 60)
        score += (24 - hoursSincePost.coerceIn(0, 24)).toFloat() * 2

        // Engagement
        score += post.likes * 0.5f
        score += post.commentCount * 1.5f

        // Personal relevance (e.g., following - simplified)
        if (post.userId == userId) score -= 100f // Show others' posts first in general feed

        return score
    }
}
