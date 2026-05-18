package com.spotshare.presentation.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomRatingBar(
    rating: Float,
    onRatingChange: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    starSize: Dp = 40.dp,
    activeColor: Color = Color(0xFFFFD700), // Gold
    inactiveColor: Color = Color.LightGray,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(starCount) { index ->
            val starIndex = index + 1
            val isFilled = rating >= starIndex
            val isHalfFilled = rating >= starIndex - 0.5f && rating < starIndex
            
            Icon(
                imageVector = when {
                    isFilled -> Icons.Filled.Star
                    isHalfFilled -> Icons.Filled.StarHalf
                    else -> Icons.Outlined.StarOutline
                },
                contentDescription = "Star $starIndex",
                modifier = Modifier
                    .size(starSize)
                    .clickable(enabled = enabled && onRatingChange != null) {
                        onRatingChange?.invoke(starIndex.toFloat())
                    },
                tint = if (isFilled || isHalfFilled) activeColor else inactiveColor
            )
        }
    }
}
