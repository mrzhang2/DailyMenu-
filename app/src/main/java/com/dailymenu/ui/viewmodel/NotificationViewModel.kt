package com.dailymenu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailymenu.data.model.Notification
import com.dailymenu.data.model.NotificationType
import com.dailymenu.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(NotificationTab.ALL)
    val selectedTab: StateFlow<NotificationTab> = _selectedTab.asStateFlow()

    val allNotifications: Flow<List<Notification>> = notificationRepository.getAllNotifications()

    val unreadCount: Flow<Int> = notificationRepository.getUnreadCount()

    val filteredNotifications: Flow<List<Notification>> = combine(
        allNotifications,
        selectedTab
    ) { notifications, tab ->
        when (tab) {
            NotificationTab.ALL -> notifications
            NotificationTab.COMMENT -> notifications.filter { it.type == NotificationType.COMMENT }
            NotificationTab.LIKE -> notifications.filter { it.type == NotificationType.LIKE }
            NotificationTab.SYSTEM -> notifications.filter { it.type == NotificationType.SYSTEM }
        }
    }

    val tabs = listOf(
        NotificationTab.ALL to "全部",
        NotificationTab.COMMENT to "评论",
        NotificationTab.LIKE to "点赞",
        NotificationTab.SYSTEM to "系统"
    )

    fun setTab(tab: NotificationTab) {
        _selectedTab.value = tab
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notificationId)
        }
    }
}

enum class NotificationTab {
    ALL, COMMENT, LIKE, SYSTEM
}
