package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: NotificationType,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val relatedId: Long? = null,
    val isRead: Boolean = false,
    val createTime: Long = System.currentTimeMillis()
)

enum class NotificationType {
    SYSTEM,
    COMMENT,
    LIKE,
    FOLLOW
}
