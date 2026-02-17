package com.dailymenu.data.database

import androidx.room.TypeConverter
import com.dailymenu.data.model.DifficultyLevel
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.MemberLevel
import com.dailymenu.data.model.RecipeCategory
import com.dailymenu.data.model.Season
import com.dailymenu.data.model.VideoChapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromRecipeCategory(category: RecipeCategory): String {
        return category.name
    }

    @TypeConverter
    fun toRecipeCategory(value: String): RecipeCategory {
        return try {
            RecipeCategory.valueOf(value)
        } catch (e: IllegalArgumentException) {
            RecipeCategory.CHINESE
        }
    }

    @TypeConverter
    fun fromMealType(mealType: MealType): String {
        return mealType.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return try {
            MealType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MealType.LUNCH
        }
    }

    @TypeConverter
    fun fromSeason(season: Season): String {
        return season.name
    }

    @TypeConverter
    fun toSeason(value: String): Season {
        return try {
            Season.valueOf(value)
        } catch (e: IllegalArgumentException) {
            Season.ALL_YEAR
        }
    }

    // VideoChapter List 转换器
    @TypeConverter
    fun fromVideoChapterList(value: List<VideoChapter>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toVideoChapterList(value: String): List<VideoChapter> {
        val listType = object : TypeToken<List<VideoChapter>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    // DifficultyLevel 转换器
    @TypeConverter
    fun fromDifficultyLevel(difficulty: DifficultyLevel): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficultyLevel(value: String): DifficultyLevel {
        return try {
            DifficultyLevel.valueOf(value)
        } catch (e: IllegalArgumentException) {
            DifficultyLevel.MEDIUM
        }
    }

    // MemberLevel 转换器
    @TypeConverter
    fun fromMemberLevel(memberLevel: MemberLevel): String {
        return memberLevel.name
    }

    @TypeConverter
    fun toMemberLevel(value: String): MemberLevel {
        return try {
            MemberLevel.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MemberLevel.FREE
        }
    }
}