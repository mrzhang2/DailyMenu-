package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dailymenu.data.database.Converters

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey
    val id: String,
    val wechatOpenId: String? = null,
    val nickname: String,
    val avatarUrl: String? = null,
    val phone: String? = null,
    val gender: Int = 0, // 0: 未知, 1: 男, 2: 女
    val region: String? = null,
    val bio: String? = null,
    val birthday: Long? = null,

    // 饮食偏好和过敏源
    val dietaryPreferences: List<String> = emptyList(),
    val allergens: List<String> = emptyList(),

    // 统计数据
    val favoriteCount: Int = 0,
    val learnedCount: Int = 0,
    val postCount: Int = 0,
    val streakDays: Int = 0,        // 连续打卡天数
    val totalCookingDays: Int = 0,  // 累计烹饪天数

    // 会员信息
    val memberLevel: MemberLevel = MemberLevel.FREE,
    val memberExpireTime: Long? = null,

    // 时间戳
    val createTime: Long = System.currentTimeMillis(),
    val lastLoginTime: Long = System.currentTimeMillis(),

    // 用户设置（JSON存储）
    val settingsJson: String? = null
)

// 用户设置数据类（不直接存储到数据库）
data class UserSettings(
    val pushEnabled: Boolean = true,
    val dailyReminderTime: String? = null, // "08:00" 格式
    val autoPlayVideo: Boolean = true,
    val videoQuality: VideoQuality = VideoQuality.AUTO,
    val language: String = "zh-CN",
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val dailyBudget: Float = 50f // 每日预算
)

enum class VideoQuality {
    LOW,    // 流畅
    MEDIUM, // 标清
    HIGH,   // 高清
    AUTO    // 自动
}

enum class DarkMode {
    LIGHT,  // 浅色
    DARK,   // 深色
    SYSTEM  // 跟随系统
}
