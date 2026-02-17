package com.dailymenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.StepImage
import com.dailymenu.ui.components.ServingsCalculator
import com.dailymenu.ui.components.StepTimer
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onNavigateBack: () -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val dailyMenu by viewModel.dailyMenu.collectAsStateWithLifecycle()
    
    // 找到对应的菜谱
    val recipe = remember(dailyMenu, recipeId) {
        dailyMenu?.let { menu ->
            listOf(menu.breakfast, menu.lunch, menu.dinner).find { it.id == recipeId }
        }
    }
    
    recipe?.let { 
        RecipeDetailContent(
            recipe = it,
            onNavigateBack = onNavigateBack,
            onFavoriteClick = { viewModel.toggleFavorite(it.id, !it.isFavorite) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailContent(
    recipe: Recipe,
    onNavigateBack: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "菜谱详情",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏",
                            tint = if (recipe.isFavorite) ErrorRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCream
                )
            )
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WarmCream),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = PrimaryOrange
                )
            }
            
            // 标题区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // 信息卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceWhite
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoItem(
                            icon = Icons.Default.Schedule,
                            label = "烹饪时间",
                            value = "${recipe.cookingTime}分钟"
                        )
                        InfoItem(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "热量",
                            value = "${recipe.calories}卡"
                        )
                        InfoItem(
                            icon = Icons.Default.RestaurantMenu,
                            label = "食材",
                            value = "${recipe.ingredients.size}种"
                        )
                    }
                }
                
                // 标签
                if (recipe.tags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recipe.tags.forEach { tag ->
                            AssistChip(
                                onClick = { },
                                label = { Text(tag) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = PrimaryOrangeLight.copy(alpha = 0.3f),
                                    labelColor = PrimaryOrangeDark
                                )
                            )
                        }
                    }
                }
                
                // 食材清单（使用份量计算器）
                SectionTitle("食材清单")
                ServingsCalculator(
                    ingredients = recipe.ingredients,
                    defaultServings = recipe.servings.coerceIn(1, 20),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 制作步骤（使用步骤图片项）
                SectionTitle("制作步骤")
                
                // 构建步骤图片映射（按步骤编号）
                val stepImagesMap = remember(recipe.stepImages) {
                    // 这里假设 stepImages 列表的顺序与步骤对应
                    // 实际项目中可能需要根据 stepNumber 来映射
                    recipe.stepImages.mapIndexed { index, imageUrl ->
                        index + 1 to StepImage(
                            id = index.toLong(),
                            recipeId = recipe.id,
                            stepNumber = index + 1,
                            imageUrl = imageUrl,
                            description = null,
                            tips = null,
                            duration = null
                        )
                    }.toMap()
                }
                
                recipe.steps.forEachIndexed { index, step ->
                    val stepNumber = index + 1
                    val stepImage = stepImagesMap[stepNumber]
                    
                    StepImageItem(
                        stepNumber = stepNumber,
                        stepText = step,
                        stepImage = stepImage
                    )
                    if (index < recipe.steps.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryOrange,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

// 简单的 FlowRow 实现
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hGapPx = 8
        val vGapPx = 8
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()
        
        var rowMeasurables = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0
        var rowHeight = 0
        
        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)
            
            if (rowWidth + placeable.width > constraints.maxWidth && rowMeasurables.isNotEmpty()) {
                rows.add(rowMeasurables)
                rowWidths.add(rowWidth)
                rowHeights.add(rowHeight)
                rowMeasurables = mutableListOf()
                rowWidth = 0
                rowHeight = 0
            }
            
            rowMeasurables.add(placeable)
            rowWidth += placeable.width + hGapPx
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        
        if (rowMeasurables.isNotEmpty()) {
            rows.add(rowMeasurables)
            rowWidths.add(rowWidth - hGapPx)
            rowHeights.add(rowHeight)
        }
        
        val height = rowHeights.sum() + (rows.size - 1) * vGapPx
        
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                var x = when (horizontalArrangement) {
                    Arrangement.Center -> (constraints.maxWidth - rowWidths[rowIndex]) / 2
                    Arrangement.End -> constraints.maxWidth - rowWidths[rowIndex]
                    else -> 0
                }
                
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + hGapPx
                }
                y += rowHeights[rowIndex] + vGapPx
            }
        }
    }
}

/**
 * 步骤图片项组件
 * 显示步骤编号、步骤文字说明、步骤图片（如果有）、小贴士（如果有）、计时器（如果有）
 */
@Composable
private fun StepImageItem(
    stepNumber: Int,
    stepText: String,
    stepImage: StepImage?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 步骤编号
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }
                
                // 步骤内容
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    
                    // 图片描述（如果有）
                    stepImage?.description?.let { desc ->
                        if (desc.isNotBlank()) {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
            
            // 步骤图片（如果有）
            stepImage?.imageUrl?.let { imageUrl ->
                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "步骤${stepNumber}图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            // 小贴士（如果有）
            stepImage?.tips?.let { tip ->
                if (tip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SecondaryYellow.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = SecondaryYellowDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            // 计时器（如果有duration）
            stepImage?.duration?.let { duration ->
                if (duration > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    StepTimer(durationSeconds = duration)
                }
            }
        }
    }
}