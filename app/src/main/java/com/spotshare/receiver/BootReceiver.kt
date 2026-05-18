package com.spotshare.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.spotshare.scheduler.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule alarms after reboot
            alarmScheduler.scheduleDailyReminder(9, 0)
        }
    }
}
