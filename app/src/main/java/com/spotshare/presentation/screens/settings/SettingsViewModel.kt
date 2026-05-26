package com.spotshare.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotshare.data.local.datastore.PreferencesManager
import com.spotshare.scheduler.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val alarmScheduler: AlarmScheduler,
    private val auth: com.google.firebase.auth.FirebaseAuth,
    private val db: com.spotshare.data.local.database.AppDatabase
) : ViewModel() {

    val darkTheme: Flow<Boolean> = preferencesManager.darkTheme
    val notificationsEnabled: Flow<Boolean> = preferencesManager.notificationsEnabled
    val searchRadius: Flow<Int> = preferencesManager.searchRadius

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkTheme(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun setSearchRadius(radius: Int) {
        viewModelScope.launch {
            preferencesManager.setSearchRadius(radius)
        }
    }

    fun scheduleDailyReminder(hour: Int, minute: Int) {
        alarmScheduler.scheduleDailyReminder(hour, minute)
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            db.clearAllTables()
        }
    }
}
