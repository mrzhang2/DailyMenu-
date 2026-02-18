package com.dailymenu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.Work
import com.dailymenu.data.repository.RecipeRepository
import com.dailymenu.data.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val workRepository: WorkRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _currentUserId = MutableStateFlow("")

    val myWorks: StateFlow<List<Work>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId.isNotEmpty()) {
                workRepository.getWorksByUser(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWorks: StateFlow<List<Work>> = workRepository.getAllWorks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recipes: StateFlow<List<Recipe>> = recipeRepository.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    private val _publishSuccess = MutableSharedFlow<Boolean>()
    val publishSuccess: SharedFlow<Boolean> = _publishSuccess.asSharedFlow()

    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun publishWork(
        recipeId: Long,
        recipeName: String,
        images: List<String>,
        description: String
    ) {
        viewModelScope.launch {
            _isPublishing.value = true
            try {
                val work = Work(
                    recipeId = recipeId,
                    recipeName = recipeName,
                    userId = _currentUserId.value,
                    userName = "用户",
                    userAvatar = null,
                    images = images,
                    description = description
                )
                workRepository.createWork(work)
                _publishSuccess.emit(true)
            } catch (e: Exception) {
                _publishSuccess.emit(false)
            } finally {
                _isPublishing.value = false
            }
        }
    }

    fun likeWork(workId: Long) {
        viewModelScope.launch {
            workRepository.likeWork(workId)
        }
    }

    fun unlikeWork(workId: Long) {
        viewModelScope.launch {
            workRepository.unlikeWork(workId)
        }
    }

    fun deleteWork(work: Work) {
        viewModelScope.launch {
            workRepository.deleteWork(work)
        }
    }
}
