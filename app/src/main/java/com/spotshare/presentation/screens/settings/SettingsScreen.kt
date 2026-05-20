package com.spotshare.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkTheme by viewModel.darkTheme.collectAsState(initial = false)
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val searchRadius by viewModel.searchRadius.collectAsState(initial = 10)
    
    SettingsContent(
        darkTheme = darkTheme,
        notificationsEnabled = notificationsEnabled,
        searchRadius = searchRadius,
        onBackClick = onBackClick,
        onDarkThemeChange = { viewModel.setDarkTheme(it) },
        onNotificationsChange = { viewModel.setNotificationsEnabled(it) },
        onSearchRadiusChange = { viewModel.setSearchRadius(it) },
        onScheduleReminder = { h, m -> viewModel.scheduleDailyReminder(h, m) },
        onLogout = {
            viewModel.logout()
            onLogoutSuccess()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    darkTheme: Boolean,
    notificationsEnabled: Boolean,
    searchRadius: Int,
    onBackClick: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onSearchRadiusChange: (Int) -> Unit,
    onScheduleReminder: (Int, Int) -> Unit,
    onLogout: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text("Dark Mode") },
                trailingContent = {
                    Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
                }
            )
            ListItem(
                headlineContent = { Text("Notifications") },
                trailingContent = {
                    Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange)
                }
            )
            
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text("Search Radius") },
                supportingContent = {
                    Column {
                        Slider(
                            value = searchRadius.toFloat(),
                            onValueChange = { onSearchRadiusChange(it.toInt()) },
                            valueRange = 1f..50f
                        )
                        Text("$searchRadius km", style = MaterialTheme.typography.bodySmall)
                    }
                }
            )

            ListItem(
                headlineContent = { Text("Reminder Time") },
                supportingContent = { Text("Set daily exploration alert") },
                trailingContent = { Text("Set", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { showTimePicker = true }
            )
            ListItem(
                headlineContent = { Text("Schedule Post") },
                supportingContent = { Text("Pick a date for future upload") },
                trailingContent = { Text("Pick", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.clickable { showDatePicker = true }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
        
        if (showTimePicker) {
            val state = rememberTimePickerState()
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = { 
                        onScheduleReminder(state.hour, state.minute)
                        showTimePicker = false 
                    }) { Text("Confirm") }
                },
                text = { TimePicker(state = state) }
            )
        }
        
        if (showDatePicker) {
            val state = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SpotShareTheme {
        SettingsContent(
            darkTheme = false,
            notificationsEnabled = true,
            searchRadius = 10,
            onBackClick = {},
            onDarkThemeChange = {},
            onNotificationsChange = {},
            onSearchRadiusChange = {},
            onScheduleReminder = { _, _ -> },
            onLogout = {}
        )
    }
}
