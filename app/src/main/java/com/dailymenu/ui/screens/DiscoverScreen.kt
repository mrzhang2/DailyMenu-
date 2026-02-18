package com.dailymenu.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dailymenu.data.model.RecipeCategory
import com.dailymenu.ui.components.RecipeGridItem
import com.dailymenu.ui.components.SearchBar
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.DiscoverViewModel
import com.dailymenu.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onRecipeClick: (Long) -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle(initialValue = "")
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle(initialValue = null)
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSearch = { },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setSelectedCategory(it) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SortOptions(
                selectedOption = sortOption,
                onOptionSelected = { viewModel.setSortOption(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (recipes.isEmpty()) {
                EmptyState(
                    hasSearchQuery = searchQuery.isNotBlank() || selectedCategory != null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = recipes,
                        key = { it.id }
                    ) { recipe ->
                        RecipeGridItem(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChips(
    selectedCategory: RecipeCategory?,
    onCategorySelected: (RecipeCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        null to "全部",
        RecipeCategory.CHINESE to "中餐",
        RecipeCategory.WESTERN to "西餐",
        RecipeCategory.JAPANESE to "日料",
        RecipeCategory.KOREAN to "韩餐",
        RecipeCategory.SOUTHEAST_ASIAN to "东南亚",
        RecipeCategory.LIGHT to "清淡",
        RecipeCategory.SPICY to "辣味",
        RecipeCategory.SOUP to "汤类",
        RecipeCategory.DESSERT to "甜品"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (category, label) ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = label,
                        fontWeight = if (selectedCategory == category) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryOrange,
                    selectedLabelColor = SurfaceWhite,
                    containerColor = SurfaceWhite,
                    labelColor = TextPrimary
                ),
                border = if (selectedCategory == category) {
                    androidx.compose.foundation.BorderStroke(1.dp, PrimaryOrange)
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, WarmBrown.copy(alpha = 0.3f))
                }
            )
        }
    }
}

@Composable
private fun SortOptions(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SortOption.entries.forEach { option ->
            val isSelected = selectedOption == option
            Surface(
                onClick = { onOptionSelected(option) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) PrimaryOrange else SurfaceWhite,
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = option.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) SurfaceWhite else TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    hasSearchQuery: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasSearchQuery) "未找到相关菜谱" else "暂无菜谱",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasSearchQuery) "试试其他关键词或分类" else "开始添加你的第一个菜谱吧",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
