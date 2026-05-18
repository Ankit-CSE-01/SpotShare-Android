package com.spotshare.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.spotshare.data.local.dao.StoryDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StoryCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val storyDao: StoryDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            storyDao.deleteExpiredStories(System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
