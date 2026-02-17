package com.dailymenu.data.repository

import com.dailymenu.data.model.*
import java.util.Calendar

class RecommendationEngine(
    private val menuRepository: MenuRepository
) {
    
    // 根据天气和预算生成每日菜单
    suspend fun generateDailyMenu(weather: WeatherInfo, budget: Float = 50f): DailyMenu {
        val date = getCurrentDate()
        val season = getCurrentSeason()
        
        // 预算分配：早餐20%，午餐40%，晚餐40%
        val breakfastBudget = budget * 0.2f
        val lunchBudget = budget * 0.4f
        val dinnerBudget = budget * 0.4f
        
        return DailyMenu(
            date = date,
            weather = weather,
            breakfast = recommendBreakfast(weather, season, breakfastBudget),
            lunch = recommendLunch(weather, season, lunchBudget),
            dinner = recommendDinner(weather, season, dinnerBudget)
        )
    }
    
    // 推荐早餐（带预算）
    private suspend fun recommendBreakfast(weather: WeatherInfo, season: Season, budget: Float): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.BREAKFAST, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.BREAKFAST, budget)
    }
    
    // 推荐午餐（带预算）
    private suspend fun recommendLunch(weather: WeatherInfo, season: Season, budget: Float): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.LUNCH, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.LUNCH, budget)
    }
    
    // 推荐晚餐（带预算）
    private suspend fun recommendDinner(weather: WeatherInfo, season: Season, budget: Float): Recipe {
        val candidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.DINNER, listOf(season, Season.ALL_YEAR))
        return selectBestRecipe(candidates, weather, MealType.DINNER, budget)
    }
    
    // 选择最佳食谱（带预算约束）
    private fun selectBestRecipe(candidates: List<Recipe>, weather: WeatherInfo, mealType: MealType, budget: Float): Recipe {
        if (candidates.isEmpty()) {
            return createDefaultRecipe(mealType, budget)
        }
        
        // 过滤掉严重超出预算的菜品（允许20%弹性）
        val affordableCandidates = candidates.filter { it.cost <= budget * 1.2f }
        val targetList = if (affordableCandidates.isNotEmpty()) affordableCandidates else candidates
        
        // 评分系统
        val scoredRecipes = targetList.map { recipe ->
            val score = calculateRecipeScore(recipe, weather, budget)
            recipe to score
        }
        
        // 根据评分排序，加入一些随机性
        return scoredRecipes
            .sortedByDescending { it.second }
            .take(5)
            .random()
            .first
    }
    
    // 计算食谱评分（带预算评分）
    private fun calculateRecipeScore(recipe: Recipe, weather: WeatherInfo, budget: Float): Int {
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
        
        // 预算评分
        val budgetRatio = recipe.cost / budget
        when {
            budgetRatio <= 0.3f -> score += 15 // 高性价比，大幅加分
            budgetRatio <= 0.7f -> score += 5  // 价格适中，正常加分
            budgetRatio <= 1.0f -> score += 0  // 刚好在预算内
            budgetRatio <= 1.2f -> score -= 10 // 稍微超支，减分
            else -> score -= 30                // 严重超支，大幅减分
        }
        
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
    
    // 生成考虑预算优化的菜单（尝试多种组合，选择性价比最高的）
    suspend fun generateBudgetConsciousMenu(weather: WeatherInfo, budget: Float = 50f): DailyMenu {
        val date = getCurrentDate()
        val season = getCurrentSeason()
        
        // 获取各餐候选菜品
        val breakfastCandidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.BREAKFAST, listOf(season, Season.ALL_YEAR))
        val lunchCandidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.LUNCH, listOf(season, Season.ALL_YEAR))
        val dinnerCandidates = menuRepository.getRecipesByMealTypeAndSeason(MealType.DINNER, listOf(season, Season.ALL_YEAR))
        
        // 尝试不同的预算分配比例
        val budgetRatios = listOf(
            Triple(0.2f, 0.4f, 0.4f),  // 标准分配
            Triple(0.15f, 0.45f, 0.4f), // 早餐少，午餐多
            Triple(0.25f, 0.35f, 0.4f), // 早餐多，午餐少
            Triple(0.2f, 0.35f, 0.45f), // 晚餐多
            Triple(0.2f, 0.45f, 0.35f)  // 午餐多，晚餐少
        )
        
        // 生成多种菜单组合并评分
        val menuOptions = budgetRatios.map { (bRatio, lRatio, dRatio) ->
            val breakfast = selectBestRecipe(breakfastCandidates, weather, MealType.BREAKFAST, budget * bRatio)
            val lunch = selectBestRecipe(lunchCandidates, weather, MealType.LUNCH, budget * lRatio)
            val dinner = selectBestRecipe(dinnerCandidates, weather, MealType.DINNER, budget * dRatio)
            
            val totalCost = breakfast.cost + lunch.cost + dinner.cost
            val totalScore = calculateRecipeScore(breakfast, weather, budget * bRatio) +
                           calculateRecipeScore(lunch, weather, budget * lRatio) +
                           calculateRecipeScore(dinner, weather, budget * dRatio)
            
            // 性价比 = 总评分 / 总成本（考虑预算约束）
            val valueForMoney = if (totalCost <= budget) {
                totalScore.toFloat() / totalCost * 1.5f // 在预算内，给1.5倍奖励
            } else {
                totalScore.toFloat() / totalCost * 0.5f // 超预算，减分
            }
            
            Triple(
                DailyMenu(date, weather, breakfast, lunch, dinner),
                totalCost,
                valueForMoney
            )
        }
        
        // 选择性价比最高的组合
        return menuOptions
            .filter { it.second <= budget * 1.1f } // 只允许10%的超支
            .maxByOrNull { it.third }
            ?.first
            ?: generateDailyMenu(weather, budget) // 回退到标准生成方法
    }

    // 创建默认食谱（当数据库为空时，考虑预算）
    private fun createDefaultRecipe(mealType: MealType, budget: Float = 50f): Recipe {
        // 根据预算调整默认价格
        val defaultCost = when (mealType) {
            MealType.BREAKFAST -> budget * 0.8f // 默认比预算低一点
            MealType.LUNCH -> budget * 0.9f
            MealType.DINNER -> budget * 0.9f
        }.coerceAtLeast(5f) // 最低5元
        
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
                cost = defaultCost,
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
                cost = defaultCost,
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
                cost = defaultCost,
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