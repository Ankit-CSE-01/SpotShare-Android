package com.spotshare.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.spotshare.data.local.dao.PostDao
import com.spotshare.data.mapper.toPost
import com.spotshare.data.worker.UploadWorker
import com.spotshare.domain.model.Post
import com.spotshare.domain.repository.PostRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    @ApplicationContext private val context: Context
) : PostRepository {

    override fun getPostsPagingFlow(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { postDao.getPostsPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toPost() }
        }
    }

    override suspend fun uploadPost(post: Post, mediaUris: List<String>): Result<Unit> {
        // Implementation for upload
        return Result.success(Unit)
    }

    override fun enqueueUpload(post: Post, mediaUris: List<String>) {
        val workManager = WorkManager.getInstance(context)
        mediaUris.forEachIndexed { index, uri ->
            val data = Data.Builder()
                .putString("media_uri", uri)
                .putString("path", "posts/${post.id}/media_$index")
                .build()

            val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(data)
                .build()

            workManager.enqueue(uploadRequest)
        }
    }
}
