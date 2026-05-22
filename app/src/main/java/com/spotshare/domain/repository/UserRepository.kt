package com.spotshare.domain.repository

import com.spotshare.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    suspend fun followUser(userId: String): Result<Unit>
    suspend fun unfollowUser(userId: String): Result<Unit>
    fun getFollowers(userId: String): Flow<List<User>>
    fun getFollowing(userId: String): Flow<List<User>>
    suspend fun updateProfile(user: User): Result<Unit>
    suspend fun isUsernameUnique(username: String): Boolean
}
