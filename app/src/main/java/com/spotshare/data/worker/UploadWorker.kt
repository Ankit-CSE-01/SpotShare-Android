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
        val mediaUri = inputData.getString("media_uri") ?: return Result.failure()
        val path = inputData.getString("path") ?: return Result.failure()

        return try {
            val storage = FirebaseStorage.getInstance()
            val ref = storage.reference.child(path)
            ref.putFile(Uri.parse(mediaUri)).await()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
