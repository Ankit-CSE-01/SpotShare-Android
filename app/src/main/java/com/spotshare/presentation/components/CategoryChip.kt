package com.spotshare.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spotshare.domain.model.SpotCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: SpotCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(category.name) },
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}
