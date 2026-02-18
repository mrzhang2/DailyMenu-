package com.dailymenu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.SecondaryYellow
import com.dailymenu.ui.theme.TextSecondary

@Composable
fun RatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val isFilled = i <= rating
            val isHalfFilled = i - 0.5f <= rating && i > rating

            val icon = when {
                isFilled -> Icons.Filled.Star
                isHalfFilled -> Icons.Filled.Star
                else -> Icons.Outlined.Star
            }

            val tint = when {
                isFilled -> SecondaryYellow
                isHalfFilled -> SecondaryYellow.copy(alpha = 0.5f)
                else -> TextSecondary.copy(alpha = 0.3f)
            }

            if (enabled) {
                Icon(
                    imageVector = icon,
                    contentDescription = "评分 $i",
                    modifier = Modifier
                        .size(starSize)
                        .clickable { onRatingChange(i.toFloat()) },
                    tint = tint
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = "评分 $i",
                    modifier = Modifier.size(starSize),
                    tint = tint
                )
            }
        }
    }
}
