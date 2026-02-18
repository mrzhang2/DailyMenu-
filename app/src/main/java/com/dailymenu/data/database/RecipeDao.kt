package com.dailymenu.data.database

import androidx.room.*
import com.dailymenu.data.model.MealType
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.Season
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes WHERE mealType = :mealType")
    fun getRecipesByMealType(mealType: MealType): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE mealType = :mealType AND season IN (:seasons)")
    suspend fun getRecipesByMealTypeAndSeason(mealType: MealType, seasons: List<Season>): List<Recipe>

    @Query("SELECT * FROM recipes WHERE mealType = :mealType AND isHot = :isHot")
    suspend fun getRecipesByTemperature(mealType: MealType, isHot: Boolean): List<Recipe>

    @Query("SELECT * FROM recipes WHERE mealType = :mealType AND isRainy = :isRainy")
    suspend fun getRecipesByRainy(mealType: MealType, isRainy: Boolean): List<Recipe>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Query("SELECT COUNT(*) FROM recipes WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE category = :category")
    fun getRecipesByCategory(category: com.dailymenu.data.model.RecipeCategory): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE category = :category AND (name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%')")
    fun searchRecipesByCategory(query: String, category: com.dailymenu.data.model.RecipeCategory): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY viewCount DESC")
    fun getRecipesByPopularity(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY createTime DESC")
    fun getRecipesByNewest(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes ORDER BY rating DESC")
    fun getRecipesByRating(): Flow<List<Recipe>>
}