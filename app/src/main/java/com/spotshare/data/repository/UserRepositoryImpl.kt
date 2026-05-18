package com.spotshare.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.spotshare.data.remote.dto.UserDto
import com.spotshare.data.remote.dto.toDto
import com.spotshare.domain.model.User
import com.spotshare.domain.repository.UserRepository
import com.spotshare.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    override fun getUser(userId: String): Flow<User?> = callbackFlow {
        val subscription = firestore.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val user = snapshot?.toObject(UserDto::class.java)?.toUser()
                trySend(user)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun followUser(userId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            
            firestore.runBatch { batch ->
                // Add to current user's following
                val currentUserRef = firestore.collection(Constants.USERS_COLLECTION).document(currentUserId)
                batch.update(currentUserRef, "followingCount", FieldValue.increment(1))
                
                // Add to target user's followers
                val targetUserRef = firestore.collection(Constants.USERS_COLLECTION).document(userId)
                batch.update(targetUserRef, "followersCount", FieldValue.increment(1))
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(userId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            
            firestore.runBatch { batch ->
                val currentUserRef = firestore.collection(Constants.USERS_COLLECTION).document(currentUserId)
                batch.update(currentUserRef, "followingCount", FieldValue.increment(-1))
                
                val targetUserRef = firestore.collection(Constants.USERS_COLLECTION).document(userId)
                batch.update(targetUserRef, "followersCount", FieldValue.increment(-1))
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFollowers(userId: String): Flow<List<User>> = callbackFlow {
        // Implementation for fetching followers
        trySend(emptyList())
        awaitClose { }
    }

    override fun getFollowing(userId: String): Flow<List<User>> = callbackFlow {
        // Implementation for fetching following
        trySend(emptyList())
        awaitClose { }
    }

    override suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            val userDto = user.toDto()
            firestore.collection(Constants.USERS_COLLECTION).document(user.uid).set(userDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
