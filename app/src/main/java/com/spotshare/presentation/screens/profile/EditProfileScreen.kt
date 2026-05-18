package com.spotshare.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.spotshare.domain.model.User
import com.spotshare.presentation.theme.SpotShareTheme

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    
    EditProfileContent(
        user = user,
        onBackClick = onBackClick,
        onSaveClick = { name, username, bio, website -> 
            viewModel.updateProfile(name, username, bio, website)
            onSaveSuccess() 
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    user: User?,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var username by remember { mutableStateOf(user?.userName ?: "") }
    var pronouns by remember { mutableStateOf("") }
    var showPronouns by remember { mutableStateOf(false) }
    var bio by remember { mutableStateOf(user?.bio ?: "") }
    var website by remember { mutableStateOf(user?.website ?: "") }
    var gender by remember { mutableStateOf("Male") }

    var showPhotoSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, "Cancel")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        onSaveClick(name, username, bio, website)
                    }) {
                        Icon(Icons.Default.Check, "Save", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Photo Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = user?.profilePicUrl ?: "https://via.placeholder.com/150",
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                TextButton(onClick = { showPhotoSheet = true }) {
                    Text("Change profile photo", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

            // Form Fields
            EditField(label = "Name", value = name, onValueChange = { name = it })
            EditField(label = "Username", value = username, onValueChange = { username = it }, helperText = "Usernames can only contain letters, numbers, underscores and periods.")
            EditField(label = "Pronouns", value = pronouns, onValueChange = { pronouns = it })
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = showPronouns, onCheckedChange = { showPronouns = it })
                Text("Show on profile", fontSize = 14.sp)
            }

            EditField(
                label = "Bio", 
                value = bio, 
                onValueChange = { if (it.length <= 150) bio = it }, 
                isMultiline = true,
                placeholder = "Add a bio to your profile",
                characterCount = "${bio.length}/150"
            )

            EditField(label = "Links", value = website, onValueChange = { website = it }, isLink = true)

            ListItem(
                headlineContent = { Text("Gender", fontSize = 14.sp, color = Color.Gray) },
                supportingContent = { Text(gender, fontSize = 16.sp) },
                trailingContent = { Icon(Icons.Default.KeyboardArrowDown, null) },
                modifier = Modifier.clickable { /* Show gender picker */ }
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp, color = Color.LightGray)

            TextButton(
                onClick = { /* Switch account */ },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Switch to Professional Account", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            ListItem(
                headlineContent = { Text("Personal Information Settings", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray) },
                modifier = Modifier.clickable { /* Navigate */ }
            )
        }

        if (showPhotoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPhotoSheet = false },
                sheetState = sheetState
            ) {
                ChangePhotoContent(
                    onNewPhoto = { showPhotoSheet = false },
                    onRemovePhoto = { showPhotoSheet = false },
                    onCancel = { showPhotoSheet = false }
                )
            }
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    helperText: String? = null,
    isMultiline: Boolean = false,
    placeholder: String? = null,
    characterCount: String? = null,
    isLink: Boolean = false
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.LightGray,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),
            placeholder = placeholder?.let { { Text(it) } },
            minLines = if (isMultiline) 3 else 1,
            maxLines = if (isMultiline) 5 else 1,
            trailingIcon = if (isLink && value.isNotEmpty()) {
                { Icon(Icons.Default.Close, null, modifier = Modifier.clickable { onValueChange("") }) }
            } else null
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (helperText != null) {
                Text(text = helperText, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp).weight(1f))
            }
            if (characterCount != null) {
                Text(text = characterCount, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun ChangePhotoContent(
    onNewPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
    ) {
        Text(
            text = "Change Profile Photo",
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(thickness = 0.5.dp)
        
        PhotoOption(text = "New Profile Photo", color = MaterialTheme.colorScheme.primary, onClick = onNewPhoto)
        PhotoOption(text = "Choose from Library", onClick = onNewPhoto)
        PhotoOption(text = "Take Photo", onClick = onNewPhoto)
        PhotoOption(text = "Choose Avatar", onClick = onNewPhoto)
        PhotoOption(text = "Remove Current Photo", color = Color.Red, onClick = onRemovePhoto)
        
        Spacer(Modifier.height(8.dp))
        PhotoOption(text = "Cancel", onClick = onCancel)
    }
}

@Composable
fun PhotoOption(text: String, color: Color = Color.Black, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        color = color,
        fontWeight = FontWeight.SemiBold,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    SpotShareTheme {
        EditProfileContent(
            user = null,
            onBackClick = {},
            onSaveClick = { _, _, _, _ -> }
        )
    }
}
