package com.dailymenu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dailymenu.data.model.LearningProgress
import com.dailymenu.ui.theme.PrimaryOrange

@Composable
fun LearningProgressCard(
    recipeName: String,
    progress: LearningProgress?,
    totalSteps: Int,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressPercent = if (progress != null && progress.videoDuration > 0) {
        (progress.videoProgress.toFloat() / progress.videoDuration.toFloat() * 100).toInt()
    } else 0

    val completedStepsCount = progress?.completedSteps?.size ?: 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (progress?.isCompleted == true) "学习完成" else "继续学习",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (progress?.isCompleted == true) 
                        PrimaryOrange 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )

                if (progress?.isCompleted == true) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "已完成",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipeName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "视频进度",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryOrange
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = progressPercent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = PrimaryOrange,
                    trackColor = MaterialTheme.colorScheme.surface,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已完成步骤: $completedStepsCount / $totalSteps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (progress?.isCompleted == true) "再看一遍" else "继续学习"
                )
            }
        }
    }
}
