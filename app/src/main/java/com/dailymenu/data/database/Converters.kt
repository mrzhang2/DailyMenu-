package com.dailymenu.data.database

import androidx.room.TypeConverter
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.RecipeCategory
import com.dailymenu.data.model.Season
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
}