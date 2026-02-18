package com.dailymenu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailymenu.data.model.Comment
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.TextSecondary

@Composable
fun CommentList(
    comments: List<Comment>,
    onLike: (Long) -> Unit,
    onReply: (Comment) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (comments.isEmpty()) {
        EmptyCommentState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = comments,
                key = { it.id }
            ) { comment ->
                CommentItem(
                    comment = comment,
                    onLike = { onLike(comment.id) },
                    onReply = { onReply(comment) }
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = PrimaryOrange.copy(alpha = 0.1f)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onLoadMore) {
                        Text(
                            text = "加载更多评论",
                            color = PrimaryOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCommentState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextSecondary.copy(alpha = 0.5f)
        )
        Text(
            text = "暂无评论",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Text(
            text = "快来抢沙发吧~",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary.copy(alpha = 0.7f)
        )
    }
}
