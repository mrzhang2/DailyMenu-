package com.dailymenu.data.repository

import com.dailymenu.data.model.*
import java.util.Calendar

class RecommendationEngine(
    private val menuRepository: MenuRepository
) {
    
    // 根据天气生成每日菜单
    suspend fun generateDailyMenu(weather: WeatherInfo): DailyMenu {
        val date = getCurrentDate()
        val season = getCurrentSeason()
        
        return DailyMenu(
            date = date,
            weather = weather,
            breakfast = recommendBreakfast(weather, season),
            lunch = recommendLunch(weather, season),
            dinner = recommendDinner(weather, season)
        )
    }
    
    // 推荐早餐
    private suspend fun recommendBreakfast(weather: WeatherInfo, season: Season): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.BREAKFAST, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.BREAKFAST)
    }
    
    // 推荐午餐
    private suspend fun recommendLunch(weather: WeatherInfo, season: Season): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.LUNCH, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.LUNCH)
    }
    
    // 推荐晚餐
    private suspend fun recommendDinner(weather: WeatherInfo, season: Season): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.DINNER, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.DINNER)
    }
    
    // 选择最佳食谱
    private fun selectBestRecipe(candidates: List<Recipe>, weather: WeatherInfo, mealType: MealType): Recipe {
        if (candidates.isEmpty()) {
            return createDefaultRecipe(mealType)
        }
        
        // 评分系统
        val scoredRecipes = candidates.map { recipe ->
            val score = calculateRecipeScore(recipe, weather)
            recipe to score
        }
        
        // 根据评分排序，加入一些随机性
        return scoredRecipes
            .sortedByDescending { it.second }
            .take(5)
            .random()
            .first
    }
    
    // 计算食谱评分
    private fun calculateRecipeScore(recipe: Recipe, weather: WeatherInfo): Int {
        var score = 50 // 基础分
        
        // 温度匹配
        when {
            weather.temperature >= 30 -> {
                if (recipe.isHot) score += 10 // 热天吃热的反而少（逆序）
                if (recipe.category == RecipeCategory.LIGHT) score += 15
                if (recipe.tags.contains("凉拌") || recipe.tags.contains("凉")) score += 20
            }
            weather.temperature <= 10 -> {
                if (recipe.isCold) score += 10 // 冷天吃热的
                if (recipe.category == RecipeCategory.SOUP) score += 15
                if (recipe.tags.contains("热") || recipe.tags.contains("暖")) score += 20
            }
            else -> score += 10 // 温度适宜
        }
        
        // 天气状况匹配
        when (weather.condition) {
            WeatherCondition.RAINY -> {
                if (recipe.isRainy) score += 15
                if (recipe.category == RecipeCategory.SOUP) score += 10
            }
            WeatherCondition.SUNNY -> {
                if (recipe.isSunny) score += 10
                if (recipe.category == RecipeCategory.LIGHT) score += 10
            }
            else -> score += 5
        }
        
        // 营养均衡加分
        if (recipe.tags.contains("营养均衡")) score += 10
        if (recipe.calories in 300..600) score += 5 // 适中的热量
        
        // 随机因素（让结果更多样化）
        score += (0..20).random()
        
        return score
    }
    
    // 获取当前季节
    private fun getCurrentSeason(): Season {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        return when (month) {
            3, 4, 5 -> Season.SPRING
            6, 7, 8 -> Season.SUMMER
            9, 10, 11 -> Season.AUTUMN
            else -> Season.WINTER
        }
    }
    
    // 获取当前日期字符串
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
    
    // 创建默认食谱（当数据库为空时）
    private fun createDefaultRecipe(mealType: MealType): Recipe {
        return when (mealType) {
            MealType.BREAKFAST -> Recipe(
                name = "营养早餐",
                description = "简单营养的早餐",
                category = RecipeCategory.CHINESE,
                mealType = MealType.BREAKFAST,
                ingredients = listOf("鸡蛋", "牛奶", "面包"),
                steps = listOf("煮鸡蛋", "热牛奶", "烤面包"),
                cookingTime = 15,
                calories = 300,
                imageUrl = null,
                isHot = false,
                isCold = false,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("简单", "营养")
            )
            MealType.LUNCH -> Recipe(
                name = "丰盛午餐",
                description = "营养均衡的午餐",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("米饭", "青菜", "肉类"),
                steps = listOf("煮饭", "炒菜"),
                cookingTime = 30,
                calories = 600,
                imageUrl = null,
                isHot = false,
                isCold = false,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("营养", "均衡")
            )
            MealType.DINNER -> Recipe(
                name = "清淡晚餐",
                description = "健康清淡的晚餐",
                category = RecipeCategory.CHINESE,
                mealType = MealType.DINNER,
                ingredients = listOf("粥", "小菜", "水果"),
                steps = listOf("煮粥", "准备小菜"),
                cookingTime = 25,
                calories = 400,
                imageUrl = null,
                isHot = false,
                isCold = false,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("清淡", "健康")
            )
        }
    }
}