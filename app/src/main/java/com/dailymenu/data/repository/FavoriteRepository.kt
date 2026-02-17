package com.dailymenu.data.repository

import com.dailymenu.data.database.RecipeDao
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.RecipeCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun getFavorites(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    fun getFavoritesByCategory(category: RecipeCategory): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes().map { recipes ->
            recipes.filter { it.category == category }
        }
    }

    fun getFavoritesCount(): Flow<Int> {
        return recipeDao.getFavoriteCount()
    }

    suspend fun toggleFavorite(recipeId: Long, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    suspend fun syncFavoritesToCloud(userId: String) {
        // TODO: 实现云端同步功能
        // 1. 获取所有本地收藏
        // 2. 上传到云端服务器
        // 3. 处理冲突
    }

    suspend fun syncFavoritesFromCloud(userId: String) {
        // TODO: 实现从云端拉取收藏
        // 1. 从云端获取用户收藏列表
        // 2. 合并到本地数据库
        // 3. 处理冲突
    }
}
