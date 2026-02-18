package com.dailymenu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.RecipeCategory
import com.dailymenu.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption(val displayName: String) {
    POPULAR("热门"),
    NEWEST("最新"),
    RATING("评分")
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<RecipeCategory?>(null)
    val selectedCategory: StateFlow<RecipeCategory?> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULAR)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val recipes: StateFlow<List<Recipe>> = combine(
        _searchQuery,
        _selectedCategory,
        _sortOption
    ) { query, category, sort ->
        Triple(query, category, sort)
    }.flatMapLatest { (query, category, sort) ->
        when {
            query.isNotBlank() && category != null -> {
                recipeRepository.searchRecipesByCategory(query, category)
            }
            query.isNotBlank() -> {
                recipeRepository.searchRecipes(query)
            }
            category != null -> {
                recipeRepository.getRecipesByCategory(category)
            }
            else -> {
                when (sort) {
                    SortOption.POPULAR -> recipeRepository.getRecipesByPopularity()
                    SortOption.NEWEST -> recipeRepository.getRecipesByNewest()
                    SortOption.RATING -> recipeRepository.getRecipesByRating()
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: RecipeCategory?) {
        _selectedCategory.value = category
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.updateFavoriteStatus(recipe.id, !recipe.isFavorite)
        }
    }
}
