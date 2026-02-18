package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey
    val recipeId: Long,
    val videoProgress: Long = 0,
    val videoDuration: Long = 0,
    val completedSteps: List<Int> = emptyList(),
    val lastWatchTime: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
