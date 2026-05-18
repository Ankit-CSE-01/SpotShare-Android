package com.spotshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.spotshare.data.local.datastore.PreferencesManager
import com.spotshare.data.worker.StoryCleanupWorker
import com.spotshare.notification.NotificationHelper
import com.spotshare.presentation.components.BottomNavBar
import com.spotshare.presentation.navigation.NavGraph
import com.spotshare.presentation.navigation.Screen
import com.spotshare.presentation.theme.SpotShareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHelper.createNotificationChannels()
        scheduleStoryCleanup()
        setContent {
            val isDarkMode by preferencesManager.darkTheme.collectAsState(initial = false)
            
            SpotShareTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = when (currentRoute) {
                    Screen.Feed.route, Screen.Explore.route, Screen.Reels.route, 
                    Screen.CreatePost.route, Screen.Profile.route, Screen.Map.route -> true
                    else -> false
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun scheduleStoryCleanup() {
        val request = PeriodicWorkRequestBuilder<StoryCleanupWorker>(
            12, TimeUnit.HOURS
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "StoryCleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
