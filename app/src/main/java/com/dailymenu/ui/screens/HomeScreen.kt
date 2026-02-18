package com.dailymenu.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.WeatherCondition
import com.dailymenu.ui.components.BudgetCard
import com.dailymenu.ui.components.BudgetSettingDialog
import com.dailymenu.ui.components.LoadingIndicator
import com.dailymenu.ui.components.MealCard
import com.dailymenu.ui.components.RatingBar
import com.dailymenu.ui.components.WeatherCard
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.MenuViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (MealType, Long) -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val dailyMenu by viewModel.dailyMenu.collectAsStateWithLifecycle(initialValue = null)
    val weather by viewModel.weather.collectAsStateWithLifecycle(initialValue = null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(initialValue = false)
    val error by viewModel.error.collectAsStateWithLifecycle(initialValue = null)
    val budget by viewModel.budget.collectAsStateWithLifecycle(initialValue = 50f)
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsStateWithLifecycle(initialValue = emptyList())
    val popularRecipes by viewModel.popularRecipes.collectAsStateWithLifecycle(initialValue = emptyList())
    
    var showManualWeatherDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "今日菜单",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCream
                ),
                actions = {
                    IconButton(onClick = { showBudgetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "设置预算",
                            tint = PrimaryOrange
                        )
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "收藏",
                            tint = PrimaryOrange
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = PrimaryOrange
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (dailyMenu != null) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        scope.launch {
                            isRefreshing = true
                            viewModel.refreshMenu()
                            isRefreshing = false
                        }
                    },
                    icon = { Icon(Icons.Default.Refresh, null) },
                    text = { Text("换一批") },
                    containerColor = PrimaryOrange,
                    contentColor = SurfaceWhite
                )
            }
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && dailyMenu == null) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 天气卡片
                    if (weather != null) {
                        WeatherCard(
                            weather = weather!!,
                            onManualClick = { showManualWeatherDialog = true },
                            onRefreshClick = { 
                                scope.launch {
                                    isRefreshing = true
                                    viewModel.loadMenuWithAutoWeather()
                                    isRefreshing = false
                                }
                            }
                        )
                    }
                    
                    // 预算卡片
                    dailyMenu?.let { menu ->
                        val totalCost = menu.breakfast.cost + menu.lunch.cost + menu.dinner.cost
                        BudgetCard(
                            budget = budget,
                            spent = totalCost,
                            onClick = { showBudgetDialog = true }
                        )
                    }
                    
                    // 错误提示
                    error?.let { errorMsg ->
                        ErrorCard(errorMsg = errorMsg)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 今日推荐标题
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "今日推荐",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(
                            onClick = { 
                                scope.launch {
                                    isRefreshing = true
                                    viewModel.refreshMenu()
                                    isRefreshing = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("刷新")
                        }
                    }
                    
                    // 三餐卡片
                    dailyMenu?.let { menu ->
                        MealCard(
                            mealType = MealType.BREAKFAST,
                            recipe = menu.breakfast,
                            onClick = { onRecipeClick(MealType.BREAKFAST, menu.breakfast.id) },
                            onFavoriteClick = { 
                                viewModel.toggleFavorite(menu.breakfast.id, !menu.breakfast.isFavorite) 
                            }
                        )
                        
                        MealCard(
                            mealType = MealType.LUNCH,
                            recipe = menu.lunch,
                            onClick = { onRecipeClick(MealType.LUNCH, menu.lunch.id) },
                            onFavoriteClick = { 
                                viewModel.toggleFavorite(menu.lunch.id, !menu.lunch.isFavorite) 
                            }
                        )
                        
                        MealCard(
                            mealType = MealType.DINNER,
                            recipe = menu.dinner,
                            onClick = { onRecipeClick(MealType.DINNER, menu.dinner.id) },
                            onFavoriteClick = { 
                                viewModel.toggleFavorite(menu.dinner.id, !menu.dinner.isFavorite) 
                            }
                        )
                    }
                    
                    // 热门推荐区域
                    PopularRecipesSection(
                        popularRecipes = popularRecipes.take(4),
                        onRecipeClick = { recipe ->
                            onRecipeClick(recipe.mealType, recipe.id)
                        }
                    )
                    
                    // 如果没有菜单数据，显示空状态
                    if (dailyMenu == null && !isLoading) {
                        EmptyStateContent(
                            onManualWeatherClick = { showManualWeatherDialog = true },
                            onRetryClick = {
                                scope.launch {
                                    isRefreshing = true
                                    viewModel.loadMenuWithAutoWeather()
                                    isRefreshing = false
                                }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    // 手动输入天气对话框
    if (showManualWeatherDialog) {
        ManualWeatherDialog(
            onDismiss = { showManualWeatherDialog = false },
            onConfirm = { temp, condition ->
                viewModel.setManualWeather(temp, condition)
                showManualWeatherDialog = false
            }
        )
    }
    
    // 预算设置对话框
    if (showBudgetDialog) {
        BudgetSettingDialog(
            currentBudget = budget,
            onDismiss = { showBudgetDialog = false },
            onConfirm = { newBudget ->
                viewModel.updateBudget(newBudget)
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun ErrorCard(errorMsg: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRed.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = ErrorRed
            )
            Text(
                text = errorMsg,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorRed
            )
        }
    }
}

@Composable
private fun PopularRecipesSection(
    popularRecipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit
) {
    if (popularRecipes.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            // 标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "热门推荐",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 热门菜谱网格
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                popularRecipes.chunked(2).forEach { rowRecipes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowRecipes.forEach { recipe ->
                            PopularRecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // 补齐一行
                        if (rowRecipes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularRecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        )
    ) {
        Column {
            // 图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(WarmCream),
                contentAlignment = Alignment.Center
            ) {
                if (!recipe.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = PrimaryOrange
                    )
                }
                
                // 收藏角标
                if (recipe.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ErrorRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SurfaceWhite
                        )
                    }
                }
            }
            
            // 信息区域
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = SecondaryYellow
                    )
                    Text(
                        text = "${recipe.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "• ${recipe.cookingTime}分钟",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent(
    onManualWeatherClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "暂无推荐菜单",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Text(
                text = "无法获取天气信息或生成菜单",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Text(
                text = "请检查网络连接或手动设置天气",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.7f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onManualWeatherClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryOrange
                    )
                ) {
                    Text("手动设置")
                }
                
                Button(
                    onClick = onRetryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("重试")
                }
            }
        }
    }
}

@Composable
fun ManualWeatherDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, WeatherCondition) -> Unit
) {
    var temperature by remember { mutableStateOf("20") }
    var selectedCondition by remember { mutableStateOf(WeatherCondition.SUNNY) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "手动输入天气",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 温度输入
                OutlinedTextField(
                    value = temperature,
                    onValueChange = { temperature = it.filter { char -> char.isDigit() || char == '-' } },
                    label = { Text("温度 (°C)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null
                        )
                    }
                )
                
                // 天气状况选择
                Text(
                    text = "天气状况",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Column {
                    WeatherCondition.values().forEach { condition ->
                        WeatherOptionItem(
                            condition = condition,
                            isSelected = selectedCondition == condition,
                            onSelect = { selectedCondition = condition }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val temp = temperature.toIntOrNull() ?: 20
                    onConfirm(temp, selectedCondition)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange
                )
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun WeatherOptionItem(
    condition: WeatherCondition,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val conditionText = when (condition) {
        WeatherCondition.SUNNY -> "☀️ 晴天"
        WeatherCondition.CLOUDY -> "☁️ 多云"
        WeatherCondition.OVERCAST -> "🌥️ 阴天"
        WeatherCondition.RAINY -> "🌧️ 雨天"
        WeatherCondition.SNOWY -> "❄️ 雪天"
        WeatherCondition.FOGGY -> "🌫️ 雾天"
        WeatherCondition.WINDY -> "💨 大风"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = PrimaryOrange
            )
        )
        Text(
            text = conditionText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
