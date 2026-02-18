package com.dailymenu.data.database

import androidx.room.*
import com.dailymenu.data.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE recipeId = :recipeId ORDER BY createTime DESC")
    fun getCommentsByRecipe(recipeId: Long): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE parentId IS NULL AND recipeId = :recipeId ORDER BY createTime DESC")
    fun getRootCommentsByRecipe(recipeId: Long): Flow<List<Comment>>

    @Query("SELECT * FROM comments WHERE parentId = :parentId ORDER BY createTime ASC")
    fun getReplies(parentId: Long): Flow<List<Comment>>

    @Query("SELECT COUNT(*) FROM comments WHERE recipeId = :recipeId")
    fun getCommentCount(recipeId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("UPDATE comments SET likeCount = likeCount + 1, isLiked = 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: Long)

    @Query("UPDATE comments SET likeCount = likeCount - 1, isLiked = 0 WHERE id = :commentId AND likeCount > 0")
    suspend fun unlikeComment(commentId: Long)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)
}
