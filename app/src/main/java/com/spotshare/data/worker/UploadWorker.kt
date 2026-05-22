package com.spotshare.data.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val mediaUris = inputData.getStringArray("media_uris") ?: return Result.failure()
        val postId = inputData.getString("post_id") ?: "unknown"

        return try {
            val storage = FirebaseStorage.getInstance()
            val uploadedUrls = mutableListOf<String>()

            mediaUris.forEachIndexed { index, uriString ->
                val ref = storage.reference.child("posts/$postId/media_$index.jpg")
                val uploadTask = ref.putFile(Uri.parse(uriString)).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                uploadedUrls.add(downloadUrl)
            }

            // In a real app, you'd update the Firestore document with these downloadUrls here
            // e.g., firestore.collection("posts").document(postId).update("mediaUrls", uploadedUrls)

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
