package com.spotshare.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.spotshare.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        notificationHelper.showSocialNotification(
            title = "Time to Explore!",
            message = "Check out the latest hidden spots near you today."
        )
    }
}
