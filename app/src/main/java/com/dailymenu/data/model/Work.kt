package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dailymenu.data.database.Converters

@Entity(tableName = "works")
@TypeConverters(Converters::class)
data class Work(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val recipeName: String,
    val userId: String,
    val userName: String,
    val userAvatar: String?,
    val images: List<String>,
    val description: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createTime: Long = System.currentTimeMillis()
)
