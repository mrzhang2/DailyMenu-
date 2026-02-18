package com.dailymenu.data.database

import androidx.room.*
import com.dailymenu.data.model.LearningProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress WHERE recipeId = :recipeId")
    fun getProgressByRecipe(recipeId: Long): Flow<LearningProgress?>

    @Query("SELECT * FROM learning_progress WHERE recipeId = :recipeId")
    suspend fun getProgressByRecipeOnce(recipeId: Long): LearningProgress?

    @Query("SELECT * FROM learning_progress ORDER BY lastWatchTime DESC")
    fun getAllProgress(): Flow<List<LearningProgress>>

    @Query("SELECT * FROM learning_progress WHERE isCompleted = 1 ORDER BY lastWatchTime DESC")
    fun getCompletedProgress(): Flow<List<LearningProgress>>

    @Query("SELECT * FROM learning_progress WHERE isCompleted = 0 ORDER BY lastWatchTime DESC LIMIT 1")
    fun getInProgress(): Flow<LearningProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgress)

    @Query("DELETE FROM learning_progress WHERE recipeId = :recipeId")
    suspend fun deleteProgress(recipeId: Long)

    @Query("UPDATE learning_progress SET isCompleted = 1 WHERE recipeId = :recipeId")
    suspend fun markAsCompleted(recipeId: Long)
}
