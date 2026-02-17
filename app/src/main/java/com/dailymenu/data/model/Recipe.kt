package com.dailymenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dailymenu.data.database.Converters

@Entity(tableName = "recipes")
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val category: RecipeCategory,
    val mealType: MealType,
    val ingredients: List<String>,
    val steps: List<String>,
    val cookingTime: Int, // 分钟
    val calories: Int,
    val imageUrl: String?,
    val isHot: Boolean, // 适合热天
    val isCold: Boolean, // 适合冷天
    val isRainy: Boolean, // 适合雨天
    val isSunny: Boolean, // 适合晴天
    val season: Season,
    val tags: List<String>,
    val isFavorite: Boolean = false,

    // 视频相关字段
    val videoUrl: String? = null,
    val videoDuration: Int = 0, // 秒
    val videoChapters: List<VideoChapter> = emptyList(),
    val videoCached: Boolean = false,

    // 图文相关字段
    val stepImages: List<String> = emptyList(),
    val tips: String? = null,
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val servings: Int = 2,
    val equipment: List<String> = emptyList(),

    // 社交相关字段
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val authorId: String? = null,
    val createTime: Long = System.currentTimeMillis(),
    val viewCount: Int = 0
)

enum class RecipeCategory {
    CHINESE,      // 中餐
    WESTERN,      // 西餐
    JAPANESE,     // 日料
    KOREAN,       // 韩餐
    SOUTHEAST_ASIAN, // 东南亚
    LIGHT,        // 清淡
    SPICY,        // 辣味
    SOUP,         // 汤类
    DESSERT       // 甜品
}

enum class MealType {
    BREAKFAST,    // 早餐
    LUNCH,        // 午餐
    DINNER        // 晚餐
}

enum class Season {
    SPRING,       // 春
    SUMMER,       // 夏
    AUTUMN,       // 秋
    WINTER,       // 冬
    ALL_YEAR      // 全年
}

data class DailyMenu(
    val date: String,
    val weather: WeatherInfo,
    val breakfast: Recipe,
    val lunch: Recipe,
    val dinner: Recipe
)

data class WeatherInfo(
    val temperature: Int,           // 温度（摄氏度）
    val condition: WeatherCondition, // 天气状况
    val location: String,           // 位置
    val humidity: Int = 50,         // 湿度
    val updateTime: Long = System.currentTimeMillis()
)

enum class WeatherCondition {
    SUNNY,        // 晴
    CLOUDY,       // 多云
    OVERCAST,     // 阴
    RAINY,        // 雨
    SNOWY,        // 雪
    FOGGY,        // 雾
    WINDY         // 风
}

// 视频章节数据类
data class VideoChapter(
    val title: String,
    val startTime: Int, // 开始时间（秒）
    val endTime: Int,   // 结束时间（秒）
    val description: String? = null
)