package com.dailymenu.data.repository

import com.dailymenu.data.database.RecipeDao
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.RecipeCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    fun getRecipesByMealType(mealType: MealType): Flow<List<Recipe>> {
        return recipeDao.getRecipesByMealType(mealType)
    }

    fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    suspend fun getRecipeById(id: Long): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDao.insertRecipe(recipe)
    }

    suspend fun updateFavoriteStatus(recipeId: Long, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    suspend fun getRecipeCount(): Int {
        return recipeDao.getRecipeCount()
    }

    fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
    }

    fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category)
    }

    fun searchRecipesByCategory(query: String, category: RecipeCategory): Flow<List<Recipe>> {
        return recipeDao.searchRecipesByCategory(query, category)
    }

    fun getRecipesByPopularity(): Flow<List<Recipe>> {
        return recipeDao.getRecipesByPopularity()
    }

    fun getRecipesByNewest(): Flow<List<Recipe>> {
        return recipeDao.getRecipesByNewest()
    }

    fun getRecipesByRating(): Flow<List<Recipe>> {
        return recipeDao.getRecipesByRating()
    }
}
