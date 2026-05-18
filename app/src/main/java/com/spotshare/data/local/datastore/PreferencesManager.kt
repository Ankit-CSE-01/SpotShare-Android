package com.spotshare.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spotshare_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    private val SEARCH_RADIUS_KEY = intPreferencesKey("search_radius")
    private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
    private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[DARK_THEME_KEY] ?: false }

    val searchRadius: Flow<Int> = context.dataStore.data
        .map { it[SEARCH_RADIUS_KEY] ?: 10 }

    val reminderTime: Flow<String> = context.dataStore.data
        .map { it[REMINDER_TIME_KEY] ?: "09:00" }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[NOTIFICATIONS_KEY] ?: true }

    val language: Flow<String> = context.dataStore.data
        .map { it[LANGUAGE_KEY] ?: "en" }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME_KEY] = enabled }
    }

    suspend fun setSearchRadius(radius: Int) {
        context.dataStore.edit { it[SEARCH_RADIUS_KEY] = radius }
    }

    suspend fun setReminderTime(time: String) {
        context.dataStore.edit { it[REMINDER_TIME_KEY] = time }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = language }
    }
}
