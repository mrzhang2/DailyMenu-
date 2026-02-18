package com.dailymenu.data.repository

import com.dailymenu.data.database.LearningProgressDao
import com.dailymenu.data.model.LearningProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningRepository @Inject constructor(
    private val learningProgressDao: LearningProgressDao
) {
    fun getProgress(recipeId: Long): Flow<LearningProgress?> {
        return learningProgressDao.getProgressByRecipe(recipeId)
    }

    suspend fun getProgressOnce(recipeId: Long): LearningProgress? {
        return learningProgressDao.getProgressByRecipeOnce(recipeId)
    }

    fun getAllProgress(): Flow<List<LearningProgress>> {
        return learningProgressDao.getAllProgress()
    }

    fun getCompletedProgress(): Flow<List<LearningProgress>> {
        return learningProgressDao.getCompletedProgress()
    }

    fun getInProgress(): Flow<LearningProgress?> {
        return learningProgressDao.getInProgress()
    }

    suspend fun saveProgress(progress: LearningProgress) {
        learningProgressDao.insertProgress(progress)
    }

    suspend fun updateVideoProgress(recipeId: Long, videoProgress: Long, videoDuration: Long) {
        val existing = learningProgressDao.getProgressByRecipeOnce(recipeId)
        if (existing != null) {
            learningProgressDao.insertProgress(
                existing.copy(
                    videoProgress = videoProgress,
                    videoDuration = videoDuration,
                    lastWatchTime = System.currentTimeMillis()
                )
            )
        } else {
            learningProgressDao.insertProgress(
                LearningProgress(
                    recipeId = recipeId,
                    videoProgress = videoProgress,
                    videoDuration = videoDuration
                )
            )
        }
    }

    suspend fun markStepCompleted(recipeId: Long, stepNumber: Int) {
        val existing = learningProgressDao.getProgressByRecipeOnce(recipeId)
        if (existing != null) {
            val completedSteps = existing.completedSteps.toMutableList()
            if (!completedSteps.contains(stepNumber)) {
                completedSteps.add(stepNumber)
                learningProgressDao.insertProgress(
                    existing.copy(
                        completedSteps = completedSteps,
                        lastWatchTime = System.currentTimeMillis()
                    )
                )
            }
        } else {
            learningProgressDao.insertProgress(
                LearningProgress(
                    recipeId = recipeId,
                    completedSteps = listOf(stepNumber)
                )
            )
        }
    }

    suspend fun completeLearning(recipeId: Long) {
        learningProgressDao.markAsCompleted(recipeId)
    }

    suspend fun deleteProgress(recipeId: Long) {
        learningProgressDao.deleteProgress(recipeId)
    }
}
