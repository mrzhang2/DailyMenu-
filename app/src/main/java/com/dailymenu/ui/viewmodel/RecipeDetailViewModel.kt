package com.dailymenu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.database.AppDatabase
import com.dailymenu.data.model.Comment
import com.dailymenu.data.model.LearningProgress
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.repository.CommentRepository
import com.dailymenu.data.repository.LearningRepository
import com.dailymenu.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val recipeRepository = RecipeRepository(database.recipeDao())
    private val commentRepository = CommentRepository(database.commentDao())
    private val learningRepository = LearningRepository(database.learningProgressDao())

    private val _recipeId = MutableStateFlow(0L)
    
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()
    
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()
    
    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _learningProgress = MutableStateFlow<LearningProgress?>(null)
    val learningProgress: StateFlow<LearningProgress?> = _learningProgress.asStateFlow()
    
    private val _videoProgress = MutableStateFlow(0L)
    val videoProgress: StateFlow<Long> = _videoProgress.asStateFlow()

    val favoriteRecipes = recipeRepository.getFavoriteRecipes()

    fun loadRecipe(recipeId: Long) {
        _recipeId.value = recipeId
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val loadedRecipe = recipeRepository.getRecipeById(recipeId)
                _recipe.value = loadedRecipe
                
                if (loadedRecipe != null) {
                    loadComments(recipeId)
                    loadLearningProgress(recipeId)
                }
            } catch (e: Exception) {
                _error.value = "加载菜谱失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadComments(recipeId: Long) {
        viewModelScope.launch {
            commentRepository.getComments(recipeId).collect { commentList ->
                _comments.value = commentList
                _commentCount.value = commentList.size
            }
        }
    }

    private fun loadLearningProgress(recipeId: Long) {
        viewModelScope.launch {
            learningRepository.getProgress(recipeId).collect { progress ->
                _learningProgress.value = progress
                _videoProgress.value = progress?.videoProgress ?: 0L
            }
        }
    }

    fun toggleFavorite() {
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            recipeRepository.updateFavoriteStatus(currentRecipe.id, !currentRecipe.isFavorite)
            _recipe.value = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
        }
    }

    fun addComment(content: String, rating: Float? = null) {
        val recipeId = _recipeId.value
        if (recipeId == 0L) return
        
        viewModelScope.launch {
            val comment = Comment(
                recipeId = recipeId,
                userId = "current_user",
                userName = "我",
                userAvatar = null,
                content = content,
                rating = rating
            )
            commentRepository.addComment(comment)
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            commentRepository.likeComment(commentId)
        }
    }

    fun updateVideoProgress(progress: Long, duration: Long) {
        val recipeId = _recipeId.value
        if (recipeId == 0L) return
        
        _videoProgress.value = progress
        viewModelScope.launch {
            learningRepository.updateVideoProgress(recipeId, progress, duration)
        }
    }

    fun markStepCompleted(stepNumber: Int) {
        val recipeId = _recipeId.value
        if (recipeId == 0L) return
        
        viewModelScope.launch {
            learningRepository.markStepCompleted(recipeId, stepNumber)
        }
    }

    fun startLearning() {
        val recipeId = _recipeId.value
        if (recipeId == 0L) return
        
        viewModelScope.launch {
            val currentProgress = _learningProgress.value
            if (currentProgress == null) {
                learningRepository.saveProgress(
                    LearningProgress(
                        recipeId = recipeId,
                        videoProgress = 0,
                        videoDuration = 0
                    )
                )
            }
        }
    }

    fun completeLearning() {
        val recipeId = _recipeId.value
        if (recipeId == 0L) return
        
        viewModelScope.launch {
            learningRepository.completeLearning(recipeId)
        }
    }

    fun refresh() {
        val recipeId = _recipeId.value
        if (recipeId != 0L) {
            loadRecipe(recipeId)
        }
    }
}
