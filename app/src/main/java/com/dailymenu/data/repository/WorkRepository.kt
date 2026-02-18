package com.dailymenu.data.repository

import com.dailymenu.data.database.WorkDao
import com.dailymenu.data.model.Work
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkRepository @Inject constructor(
    private val workDao: WorkDao
) {
    fun getAllWorks(): Flow<List<Work>> {
        return workDao.getAllWorks()
    }

    fun getWorksByUser(userId: String): Flow<List<Work>> {
        return workDao.getWorksByUser(userId)
    }

    fun getWorksByRecipe(recipeId: Long): Flow<List<Work>> {
        return workDao.getWorksByRecipe(recipeId)
    }

    suspend fun getWorkById(id: Long): Work? {
        return workDao.getWorkById(id)
    }

    suspend fun createWork(work: Work): Long {
        return workDao.insertWork(work)
    }

    suspend fun updateWork(work: Work) {
        workDao.updateWork(work)
    }

    suspend fun deleteWork(work: Work) {
        workDao.deleteWork(work)
    }

    suspend fun likeWork(workId: Long) {
        workDao.incrementLikeCount(workId)
    }

    suspend fun unlikeWork(workId: Long) {
        workDao.decrementLikeCount(workId)
    }

    suspend fun incrementCommentCount(workId: Long) {
        workDao.incrementCommentCount(workId)
    }

    suspend fun getWorkCountByUser(userId: String): Int {
        return workDao.getWorkCountByUser(userId)
    }
}
