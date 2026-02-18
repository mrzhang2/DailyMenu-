package com.dailymenu.data.repository

import com.dailymenu.data.database.CommentDao
import com.dailymenu.data.model.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentDao: CommentDao
) {
    fun getComments(recipeId: Long) = commentDao.getCommentsByRecipe(recipeId)

    fun getCommentCount(recipeId: Long) = commentDao.getCommentCount(recipeId)

    suspend fun addComment(comment: Comment) = commentDao.insertComment(comment)

    suspend fun likeComment(commentId: Long) = commentDao.likeComment(commentId)

    suspend fun unlikeComment(commentId: Long) = commentDao.unlikeComment(commentId)
}
