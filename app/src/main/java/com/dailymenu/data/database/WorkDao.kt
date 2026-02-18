package com.dailymenu.data.database

import androidx.room.*
import com.dailymenu.data.model.Work
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDao {
    @Query("SELECT * FROM works ORDER BY createTime DESC")
    fun getAllWorks(): Flow<List<Work>>

    @Query("SELECT * FROM works WHERE userId = :userId ORDER BY createTime DESC")
    fun getWorksByUser(userId: String): Flow<List<Work>>

    @Query("SELECT * FROM works WHERE recipeId = :recipeId ORDER BY createTime DESC")
    fun getWorksByRecipe(recipeId: Long): Flow<List<Work>>

    @Query("SELECT * FROM works WHERE id = :id")
    suspend fun getWorkById(id: Long): Work?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWork(work: Work): Long

    @Update
    suspend fun updateWork(work: Work)

    @Delete
    suspend fun deleteWork(work: Work)

    @Query("UPDATE works SET likeCount = likeCount + 1 WHERE id = :workId")
    suspend fun incrementLikeCount(workId: Long)

    @Query("UPDATE works SET likeCount = likeCount - 1 WHERE id = :workId AND likeCount > 0")
    suspend fun decrementLikeCount(workId: Long)

    @Query("UPDATE works SET commentCount = commentCount + 1 WHERE id = :workId")
    suspend fun incrementCommentCount(workId: Long)

    @Query("SELECT COUNT(*) FROM works WHERE userId = :userId")
    suspend fun getWorkCountByUser(userId: String): Int
}
