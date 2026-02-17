package com.dailymenu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.RecipeCategory
import com.dailymenu.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val categories: List<String> = listOf("全部") + RecipeCategory.values().map { category ->
        when (category) {
            RecipeCategory.CHINESE -> "中餐"
            RecipeCategory.WESTERN -> "西餐"
            RecipeCategory.JAPANESE -> "日料"
            RecipeCategory.KOREAN -> "韩餐"
            RecipeCategory.SOUTHEAST_ASIAN -> "东南亚"
            RecipeCategory.LIGHT -> "清淡"
            RecipeCategory.SPICY -> "辣味"
            RecipeCategory.SOUP -> "汤类"
            RecipeCategory.DESSERT -> "甜品"
        }
    }

    val favorites: Flow<List<Recipe>> = combine(
        favoriteRepository.getFavorites(),
        selectedCategory
    ) { recipes, category ->
        if (category == null || category == "全部") {
            recipes
        } else {
            val targetCategory = when (category) {
                "中餐" -> RecipeCategory.CHINESE
                "西餐" -> RecipeCategory.WESTERN
                "日料" -> RecipeCategory.JAPANESE
                "韩餐" -> RecipeCategory.KOREAN
                "东南亚" -> RecipeCategory.SOUTHEAST_ASIAN
                "清淡" -> RecipeCategory.LIGHT
                "辣味" -> RecipeCategory.SPICY
                "汤类" -> RecipeCategory.SOUP
                "甜品" -> RecipeCategory.DESSERT
                else -> null
            }
            targetCategory?.let { recipes.filter { it.category == targetCategory } } ?: recipes
        }
    }

    val favoritesCount: Flow<Int> = favoriteRepository.getFavoritesCount()

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(recipe.id, !recipe.isFavorite)
        }
    }
}
