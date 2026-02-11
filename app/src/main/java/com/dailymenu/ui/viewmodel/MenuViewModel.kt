package com.dailymenu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.database.AppDatabase
import com.dailymenu.data.model.*
import com.dailymenu.data.repository.MenuRepository
import com.dailymenu.data.repository.RecommendationEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = MenuRepository(application, database.recipeDao())
    private val recommendationEngine = RecommendationEngine(repository)
    
    private val _dailyMenu = MutableStateFlow<DailyMenu?>(null)
    val dailyMenu: StateFlow<DailyMenu?> = _dailyMenu.asStateFlow()
    
    private val _weather = MutableStateFlow<WeatherInfo?>(null)
    val weather: StateFlow<WeatherInfo?> = _weather.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    val favoriteRecipes = repository.getFavoriteRecipes()
    
    init {
        viewModelScope.launch {
            // 初始化数据库
            initializeDatabase()
            // 尝试自动获取天气并生成菜单
            loadMenuWithAutoWeather()
        }
    }
    
    // 初始化数据库（首次运行时填充示例数据）
    private suspend fun initializeDatabase() {
        if (repository.getRecipeCount() == 0) {
            val sampleRecipes = createSampleRecipes()
            repository.insertRecipes(sampleRecipes)
        }
    }
    
    // 自动获取天气并生成菜单
    fun loadMenuWithAutoWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getCurrentLocationWeather()
                .onSuccess { weather ->
                    _weather.value = weather
                    generateMenu(weather)
                }
                .onFailure { error ->
                    _error.value = "自动获取天气失败：${error.message}"
                    _isLoading.value = false
                }
        }
    }
    
    // 手动设置天气
    fun setManualWeather(temperature: Int, condition: WeatherCondition, location: String = "手动设置") {
        val weather = repository.createManualWeather(temperature, condition, location)
        _weather.value = weather
        viewModelScope.launch {
            generateMenu(weather)
        }
    }
    
    // 根据天气生成菜单
    private suspend fun generateMenu(weather: WeatherInfo) {
        try {
            val menu = recommendationEngine.generateDailyMenu(weather)
            _dailyMenu.value = menu
        } catch (e: Exception) {
            _error.value = "生成菜单失败：${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // 刷新菜单
    fun refreshMenu() {
        _weather.value?.let { weather ->
            viewModelScope.launch {
                _isLoading.value = true
                generateMenu(weather)
            }
        }
    }
    
    // 切换收藏状态
    fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(recipeId, isFavorite)
        }
    }
    
    // 创建示例食谱数据
    private fun createSampleRecipes(): List<Recipe> {
        return listOf(
            // 早餐
            Recipe(
                name = "皮蛋瘦肉粥",
                description = "暖胃又营养的早餐粥品",
                category = RecipeCategory.CHINESE,
                mealType = MealType.BREAKFAST,
                ingredients = listOf("大米", "皮蛋", "瘦肉", "姜丝", "葱花", "盐", "香油"),
                steps = listOf("大米洗净浸泡30分钟", "瘦肉切丝腌制", "水煮开后下米", "米烂后加入皮蛋和肉丝", "调味撒葱花"),
                cookingTime = 40,
                calories = 280,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("暖胃", "营养", "中式")
            ),
            Recipe(
                name = "西式早餐三明治",
                description = "简单快捷的营养早餐",
                category = RecipeCategory.WESTERN,
                mealType = MealType.BREAKFAST,
                ingredients = listOf("吐司面包", "鸡蛋", "生菜", "番茄", "火腿片", "芝士片", "沙拉酱"),
                steps = listOf("面包片稍微烤一下", "煎蛋和火腿", "层层叠加食材", "对角切开即可"),
                cookingTime = 10,
                calories = 350,
                imageUrl = null,
                isHot = false,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("简单", "快捷", "西式")
            ),
            Recipe(
                name = "清凉绿豆粥",
                description = "消暑解渴的夏季早餐",
                category = RecipeCategory.CHINESE,
                mealType = MealType.BREAKFAST,
                ingredients = listOf("绿豆", "大米", "冰糖", "清水"),
                steps = listOf("绿豆提前浸泡", "与大米一起煮至开花", "加入冰糖调味", "放凉后食用更佳"),
                cookingTime = 45,
                calories = 200,
                imageUrl = null,
                isHot = false,
                isCold = false,
                isRainy = false,
                isSunny = true,
                season = Season.SUMMER,
                tags = listOf("清凉", "解暑", "甜粥")
            ),
            Recipe(
                name = "豆浆油条",
                description = "经典中式早餐组合",
                category = RecipeCategory.CHINESE,
                mealType = MealType.BREAKFAST,
                ingredients = listOf("黄豆", "油条", "白糖"),
                steps = listOf("黄豆浸泡一夜", "用豆浆机打豆浆", "过滤煮沸", "搭配油条食用"),
                cookingTime = 30,
                calories = 400,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("经典", "中式", "饱腹")
            ),
            // 午餐
            Recipe(
                name = "红烧肉",
                description = "肥而不腻的经典家常菜",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("五花肉", "冰糖", "生抽", "老抽", "料酒", "八角", "桂皮", "葱姜"),
                steps = listOf("五花肉切块焯水", "炒糖色", "下肉块翻炒上色", "加水和调料炖煮", "收汁装盘"),
                cookingTime = 60,
                calories = 650,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.AUTUMN,
                tags = listOf("经典", "下饭", "肉类")
            ),
            Recipe(
                name = "清炒时蔬",
                description = "清爽解腻的青菜",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("时令蔬菜", "蒜末", "盐", "食用油"),
                steps = listOf("蔬菜洗净切段", "热锅下油爆香蒜末", "下蔬菜快炒", "调味出锅"),
                cookingTime = 5,
                calories = 80,
                imageUrl = null,
                isHot = false,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("清淡", "健康", "蔬菜")
            ),
            Recipe(
                name = "番茄鸡蛋面",
                description = "简单美味的家常面食",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("面条", "番茄", "鸡蛋", "葱花", "盐", "糖", "生抽"),
                steps = listOf("番茄切块炒出汁", "加水煮开", "下面条煮熟", "倒入蛋液", "调味撒葱花"),
                cookingTime = 15,
                calories = 450,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("简单", "面食", "家常")
            ),
            Recipe(
                name = "凉拌黄瓜",
                description = "开胃爽口的凉菜",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("黄瓜", "蒜末", "醋", "生抽", "香油", "辣椒油", "白糖"),
                steps = listOf("黄瓜拍碎切段", "加盐腌制出水", "挤干水分", "加入调料拌匀", "冷藏后更爽口"),
                cookingTime = 10,
                calories = 60,
                imageUrl = null,
                isHot = false,
                isCold = false,
                isRainy = false,
                isSunny = true,
                season = Season.SUMMER,
                tags = listOf("凉拌", "开胃", "低卡")
            ),
            Recipe(
                name = "宫保鸡丁",
                description = "香辣可口的川菜经典",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("鸡胸肉", "花生米", "干辣椒", "花椒", "葱姜蒜", "生抽", "醋", "糖", "料酒"),
                steps = listOf("鸡肉切丁腌制", "调宫保汁", "炒香辣椒花椒", "下鸡丁炒至变色", "倒入汁料和花生米", "快速翻炒均匀"),
                cookingTime = 20,
                calories = 480,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("川菜", "香辣", "下饭")
            ),
            Recipe(
                name = "酸菜鱼",
                description = "酸辣开胃的鱼片汤",
                category = RecipeCategory.CHINESE,
                mealType = MealType.LUNCH,
                ingredients = listOf("草鱼", "酸菜", "泡椒", "姜蒜", "花椒", "干辣椒", "香菜"),
                steps = listOf("鱼片腌制", "炒香酸菜泡椒", "加水煮开", "下鱼片煮熟", "淋热油激香"),
                cookingTime = 30,
                calories = 380,
                imageUrl = null,
                isHot = false,
                isCold = true,
                isRainy = true,
                isSunny = false,
                season = Season.WINTER,
                tags = listOf("酸辣", "开胃", "鱼类")
            ),
            // 晚餐
            Recipe(
                name = "清蒸鲈鱼",
                description = "鲜嫩健康的清蒸鱼",
                category = RecipeCategory.CHINESE,
                mealType = MealType.DINNER,
                ingredients = listOf("鲈鱼", "葱姜", "蒸鱼豉油", "料酒", "香油"),
                steps = listOf("鱼洗净划刀", "抹料酒姜片腌制", "水开后蒸8-10分钟", "倒掉汤汁撒葱丝", "淋豉油和热油"),
                cookingTime = 20,
                calories = 200,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("清蒸", "健康", "鱼类")
            ),
            Recipe(
                name = "冬瓜排骨汤",
                description = "清热解暑的养生汤",
                category = RecipeCategory.SOUP,
                mealType = MealType.DINNER,
                ingredients = listOf("冬瓜", "排骨", "姜片", "盐", "枸杞"),
                steps = listOf("排骨焯水", "加水和姜炖煮", "排骨熟后下冬瓜", "煮至冬瓜透明", "调味撒枸杞"),
                cookingTime = 50,
                calories = 250,
                imageUrl = null,
                isHot = false,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.SUMMER,
                tags = listOf("汤品", "清淡", "养生")
            ),
            Recipe(
                name = "麻婆豆腐",
                description = "麻辣鲜香的经典川菜",
                category = RecipeCategory.CHINESE,
                mealType = MealType.DINNER,
                ingredients = listOf("嫩豆腐", "牛肉末", "豆瓣酱", "花椒粉", "葱姜蒜", "生抽", "淀粉"),
                steps = listOf("豆腐切块焯水", "炒香肉末和豆瓣酱", "加水煮开", "下豆腐小火煮", "勾芡撒花椒粉"),
                cookingTime = 15,
                calories = 320,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("川菜", "麻辣", "豆腐")
            ),
            Recipe(
                name = "紫菜蛋花汤",
                description = "简单快捷的汤品",
                category = RecipeCategory.SOUP,
                mealType = MealType.DINNER,
                ingredients = listOf("紫菜", "鸡蛋", "虾皮", "葱花", "盐", "香油"),
                steps = listOf("水烧开", "下紫菜和虾皮", "倒入蛋液搅拌", "调味撒葱花", "淋香油出锅"),
                cookingTime = 5,
                calories = 80,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = true,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("简单", "汤品", "清淡")
            ),
            Recipe(
                name = "香菇青菜",
                description = "清淡营养的素菜",
                category = RecipeCategory.CHINESE,
                mealType = MealType.DINNER,
                ingredients = listOf("青菜", "香菇", "蒜末", "盐", "生抽", "食用油"),
                steps = listOf("香菇切片青菜洗净", "先炒香菇至软", "下青菜快炒", "调味出锅"),
                cookingTime = 8,
                calories = 90,
                imageUrl = null,
                isHot = false,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("素菜", "清淡", "健康")
            ),
            Recipe(
                name = "韩式石锅拌饭",
                description = "营养丰富的拌饭",
                category = RecipeCategory.KOREAN,
                mealType = MealType.DINNER,
                ingredients = listOf("米饭", "牛肉", "豆芽", "菠菜", "胡萝卜", "香菇", "鸡蛋", "韩式辣酱"),
                steps = listOf("蔬菜分别焯水或炒熟", "牛肉炒熟调味", "石锅抹油铺米饭", "码放食材", "中间放煎蛋", "配辣酱上桌"),
                cookingTime = 25,
                calories = 550,
                imageUrl = null,
                isHot = true,
                isCold = true,
                isRainy = false,
                isSunny = true,
                season = Season.ALL_YEAR,
                tags = listOf("韩式", "拌饭", "丰富")
            )
        )
    }
}