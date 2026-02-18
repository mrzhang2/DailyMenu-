package com.dailymenu.data.repository

import com.dailymenu.data.database.NotificationDao
import com.dailymenu.data.model.Notification
import com.dailymenu.data.model.NotificationType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getAllNotifications() = notificationDao.getAllNotifications()

    fun getNotificationsByType(type: NotificationType) = notificationDao.getNotificationsByType(type)

    fun getUnreadCount() = notificationDao.getUnreadCount()

    suspend fun markAsRead(notificationId: Long) = notificationDao.markAsRead(notificationId)

    suspend fun markAllAsRead() = notificationDao.markAllAsRead()

    suspend fun deleteNotification(notificationId: Long) = notificationDao.deleteNotification(notificationId)

    suspend fun addNotification(notification: Notification) = notificationDao.insertNotification(notification)
}
