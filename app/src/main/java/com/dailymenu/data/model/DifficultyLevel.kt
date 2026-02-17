package com.dailymenu.data.model

enum class DifficultyLevel(
    val displayName: String,
    val level: Int
) {
    EASY("简单", 1),
    MEDIUM("中等", 2),
    HARD("困难", 3),
    EXPERT("专家", 4)
}
