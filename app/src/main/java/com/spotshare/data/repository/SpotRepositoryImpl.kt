package com.spotshare.data.repository

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.spotshare.data.local.dao.SpotDao
import com.spotshare.data.local.entity.toEntity
import com.spotshare.data.remote.dto.SpotDto
import com.spotshare.data.remote.dto.toDto
import com.spotshare.domain.model.Review
import com.spotshare.domain.model.Spot
import com.spotshare.domain.repository.SpotRepository
import com.spotshare.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class SpotRepositoryImpl @Inject constructor(
    private val spotDao: SpotDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : SpotRepository {

    override fun getNearbySpots(latitude: Double, longitude: Double, radius: Double): Flow<List<Spot>> = callbackFlow {
        // First emit from local cache
        launch {
            val localSpots = spotDao.getAllSpots().first().map { it.toSpot() }
            trySend(localSpots)
        }

        // Then sync from Firestore
        val subscription = firestore.collection(Constants.SPOTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val spots = snapshot?.toObjects(SpotDto::class.java)?.map { it.toSpot() } ?: emptyList()
                
                // Update local cache
                launch {
                    spotDao.clearAll()
                    spotDao.insertSpots(spots.map { it.toEntity() })
                    trySend(spots)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getSpotById(id: String): Spot? {
        return spotDao.getSpotById(id)?.toSpot() ?: try {
            firestore.collection(Constants.SPOTS_COLLECTION).document(id).get().await()
                .toObject(SpotDto::class.java)?.toSpot()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addSpot(spot: Spot): Result<Unit> {
        return try {
            val dto = spot.toDto()
            firestore.collection(Constants.SPOTS_COLLECTION).document(dto.id).set(dto).await()
            spotDao.insertSpot(spot.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            firestore.collection(Constants.REVIEWS_COLLECTION).document(review.id).set(review).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReviewsForSpot(spotId: String): Flow<List<Review>> = callbackFlow {
        val subscription = firestore.collection(Constants.REVIEWS_COLLECTION)
            .whereEqualTo("spotId", spotId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(reviews)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun uploadImage(imageUri: String): Result<String> {
        return try {
            val ref = storage.reference.child("images/${UUID.randomUUID()}")
            ref.putFile(Uri.parse(imageUri)).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSavedSpots(userId: String): Flow<List<Spot>> = callbackFlow {
        val subscription = firestore.collection(Constants.USERS_COLLECTION).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val savedIds = snapshot?.get("savedSpots") as? List<*> ?: emptyList<String>()
                
                if (savedIds.isEmpty()) {
                    trySend(emptyList())
                } else {
                    firestore.collection(Constants.SPOTS_COLLECTION)
                        .whereIn("id", savedIds.take(10))
                        .get()
                        .addOnSuccessListener { spotSnapshots ->
                            val spots = spotSnapshots.toObjects(SpotDto::class.java).map { it.toSpot() }
                            trySend(spots)
                        }
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getCreatedSpots(userId: String): Flow<List<Spot>> = callbackFlow {
        val subscription = firestore.collection(Constants.SPOTS_COLLECTION)
            .whereEqualTo("createdBy", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val spots = snapshot?.toObjects(SpotDto::class.java)?.map { it.toSpot() } ?: emptyList()
                trySend(spots)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun toggleSaveSpot(userId: String, spotId: String): Result<Unit> {
        return try {
            val userRef = firestore.collection(Constants.USERS_COLLECTION).document(userId)
            val doc = userRef.get().await()
            val savedSpots = doc.get("savedSpots") as? List<*> ?: emptyList<String>()
            
            if (savedSpots.contains(spotId)) {
                userRef.update("savedSpots", FieldValue.arrayRemove(spotId)).await()
            } else {
                userRef.update("savedSpots", FieldValue.arrayUnion(spotId)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isSpotSaved(userId: String, spotId: String): Flow<Boolean> = callbackFlow {
        val subscription = firestore.collection(Constants.USERS_COLLECTION).document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val savedSpots = snapshot?.get("savedSpots") as? List<*> ?: emptyList<String>()
                trySend(savedSpots.contains(spotId))
            }
        awaitClose { subscription.remove() }
    }
}
