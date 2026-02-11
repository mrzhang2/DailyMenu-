package com.dailymenu.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.WeatherCondition
import com.dailymenu.ui.components.LoadingIndicator
import com.dailymenu.ui.components.MealCard
import com.dailymenu.ui.components.WeatherCard
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (MealType, Long) -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val dailyMenu by viewModel.dailyMenu.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    var showManualWeatherDialog by remember { mutableStateOf(false) }
    
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
                    onClick = { viewModel.refreshMenu() },
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
                            onRefreshClick = { viewModel.loadMenuWithAutoWeather() }
                        )
                    }
                    
                    // 错误提示
                    error?.let { errorMsg ->
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
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
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
                    
                    // 如果没有菜单数据，显示提示
                    if (dailyMenu == null && !isLoading) {
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
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "无法获取天气信息",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "请手动输入天气或检查网络连接",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Button(
                                    onClick = { showManualWeatherDialog = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryOrange
                                    )
                                ) {
                                    Text("手动输入天气")
                                }
                            }
                        }
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
        title = { Text("手动输入天气") },
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
                    modifier = Modifier.fillMaxWidth()
                )
                
                // 天气状况选择
                Text(
                    text = "天气状况",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Column {
                    WeatherCondition.values().forEach { condition ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCondition = condition }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCondition == condition,
                                onClick = { selectedCondition = condition }
                            )
                            Text(
                                text = when (condition) {
                                    WeatherCondition.SUNNY -> "☀️ 晴天"
                                    WeatherCondition.CLOUDY -> "☁️ 多云"
                                    WeatherCondition.OVERCAST -> "🌥️ 阴天"
                                    WeatherCondition.RAINY -> "🌧️ 雨天"
                                    WeatherCondition.SNOWY -> "❄️ 雪天"
                                    WeatherCondition.FOGGY -> "🌫️ 雾天"
                                    WeatherCondition.WINDY -> "💨 大风"
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val temp = temperature.toIntOrNull() ?: 20
                    onConfirm(temp, selectedCondition)
                }
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