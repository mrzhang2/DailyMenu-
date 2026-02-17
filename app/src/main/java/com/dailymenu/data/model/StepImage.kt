package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "step_images",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["recipeId"])]
)
data class StepImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val stepNumber: Int,        // 步骤编号
    val imageUrl: String,       // 图片URL
    val description: String? = null, // 图片描述
    val tips: String? = null,   // 小贴士
    val duration: Int? = null   // 该步骤时长（秒）
)
