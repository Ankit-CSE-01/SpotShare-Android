package com.spotshare.scheduler

import android.app.job.JobParameters
import android.app.job.JobService
import com.spotshare.domain.repository.FeedRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncJobService : JobService() {
    @Inject lateinit var feedRepository: FeedRepository
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onStartJob(params: JobParameters?): Boolean {
        scope.launch {
            try {
                feedRepository.refreshFeed()
                jobFinished(params, false)
            } catch (_: Exception) {
                jobFinished(params, true)
            }
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}
