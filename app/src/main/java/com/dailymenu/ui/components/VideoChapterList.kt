package com.dailymenu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dailymenu.data.model.VideoChapter
import com.dailymenu.ui.theme.PrimaryOrange

@Composable
fun VideoChapterList(
    chapters: List<VideoChapter>,
    currentPosition: Long,
    onChapterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPositionSeconds = (currentPosition / 1000).toInt()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(chapters) { index, chapter ->
            val isCurrentChapter = currentPositionSeconds >= chapter.startTime && 
                currentPositionSeconds < chapter.endTime
            
            ChapterItem(
                chapter = chapter,
                index = index + 1,
                isPlaying = isCurrentChapter,
                onClick = { onChapterClick(chapter.startTime) }
            )
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: VideoChapter,
    index: Int,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) 
                PrimaryOrange.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isPlaying) PrimaryOrange 
                        else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "正在播放",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isPlaying) PrimaryOrange else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (chapter.description != null) {
                    Text(
                        text = chapter.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = formatChapterTime(chapter.startTime),
                style = MaterialTheme.typography.labelMedium,
                color = if (isPlaying) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatChapterTime(timeSeconds: Int): String {
    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
