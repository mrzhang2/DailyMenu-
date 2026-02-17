package com.dailymenu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun StepTimer(
    durationSeconds: Int,
    modifier: Modifier = Modifier
) {
    var remainingTime by remember { mutableIntStateOf(durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    
    LaunchedEffect(isRunning) {
        if (isRunning && remainingTime > 0) {
            while (remainingTime > 0 && isRunning) {
                delay(1000)
                if (isRunning) {
                    remainingTime--
                }
            }
            if (remainingTime == 0) {
                isRunning = false
                isCompleted = true
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SuccessGreen.copy(alpha = 0.1f) else SurfaceWhite
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间显示
            Column {
                Text(
                    text = formatTime(remainingTime),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCompleted -> SuccessGreen
                        isRunning -> PrimaryOrange
                        else -> TextPrimary
                    }
                )
                Text(
                    text = when {
                        isCompleted -> "计时完成！"
                        isRunning -> "计时中..."
                        else -> "开始计时"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isCompleted -> SuccessGreen
                        else -> TextSecondary
                    }
                )
            }
            
            // 控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 播放/暂停按钮
                IconButton(
                    onClick = {
                        if (isCompleted) {
                            remainingTime = durationSeconds
                            isCompleted = false
                            isRunning = true
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> SuccessGreen
                                isRunning -> PrimaryOrangeLight
                                else -> PrimaryOrange
                            }
                        )
                ) {
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Default.Refresh
                            isRunning -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = if (isRunning) "暂停" else if (isCompleted) "重新计时" else "开始",
                        tint = if (isCompleted) SurfaceWhite else if (isRunning) PrimaryOrangeDark else SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // 重置按钮（仅在运行或暂停时显示）
                if (!isCompleted && (isRunning || remainingTime < durationSeconds)) {
                    IconButton(
                        onClick = {
                            isRunning = false
                            remainingTime = durationSeconds
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WarmCream)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "重置",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
