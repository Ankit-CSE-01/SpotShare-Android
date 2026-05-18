package com.spotshare.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.spotshare.data.local.dao.PostDao
import com.spotshare.data.local.dao.StoryDao
import com.spotshare.data.mapper.toEntity
import com.spotshare.data.mapper.toPost
import com.spotshare.data.mapper.toStory
import com.spotshare.domain.model.Post
import com.spotshare.domain.model.StoryGroup
import com.spotshare.domain.repository.FeedRepository
import com.spotshare.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val storyDao: StoryDao,
    private val firestore: FirebaseFirestore
) : FeedRepository {

    override fun getFeed(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { it.toPost() }
        }
    }

    override fun getStories(): Flow<List<StoryGroup>> {
        return storyDao.getActiveStories(System.currentTimeMillis()).map { entities ->
            entities.groupBy { it.userId }.map { (userId, userStories) ->
                StoryGroup(
                    userId = userId,
                    userName = userStories.first().userName,
                    userProfilePic = userStories.first().userProfilePic,
                    stories = userStories.map { it.toStory() },
                    hasUnviewed = userStories.any { !it.isViewed }
                )
            }
        }
    }

    override suspend fun refreshFeed(): Result<Unit> {
        return try {
            val snapshot = firestore.collection(Constants.SPOTS_COLLECTION) // Using SPOTS_COLLECTION for now, should be POSTS
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            // In a real app we'd have a PostDto. For now using logic to map Firestore docs
            // This is just a placeholder logic
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likePost(postId: String): Result<Unit> {
        // Update Firestore and Local
        return Result.success(Unit)
    }
}
