package com.spotshare.scheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobSchedulerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleSyncJob() {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(context, SyncJobService::class.java)
        val jobInfo = JobInfo.Builder(1002, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setRequiresCharging(true)
            .setPeriodic(24 * 60 * 60 * 1000) // 24 hours
            .build()

        jobScheduler.schedule(jobInfo)
    }
}
