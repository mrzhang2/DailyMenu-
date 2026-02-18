package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val parentId: Long? = null,
    val rating: Float? = null,
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val createTime: Long = System.currentTimeMillis()
)
