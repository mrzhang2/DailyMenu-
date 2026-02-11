package com.dailymenu.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.dailymenu.MainActivity
import com.dailymenu.R
import com.dailymenu.data.database.AppDatabase
import com.dailymenu.data.repository.MenuRepository
import com.dailymenu.data.repository.RecommendationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

// 每日菜单更新 Worker
class DailyMenuWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val repository = MenuRepository(applicationContext, database.recipeDao())
                val recommendationEngine = RecommendationEngine(repository)
                
                // 获取天气并生成菜单
                repository.getCurrentLocationWeather()
                    .onSuccess { weather ->
                        val menu = recommendationEngine.generateDailyMenu(weather)
                        
                        // 发送通知
                        sendMenuNotification(menu.breakfast.name, menu.lunch.name, menu.dinner.name)
                    }
                    .onFailure {
                        // 失败也发送通知提醒用户手动更新
                        sendNotification(
                            title = "今日菜单已准备好",
                            content = "点击打开应用查看今日推荐菜谱"
                        )
                    }
                
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
    
    private fun sendMenuNotification(breakfast: String, lunch: String, dinner: String) {
        val content = "早餐：$breakfast｜午餐：$lunch｜晚餐：$dinner"
        sendNotification("今日菜单推荐", content)
    }
    
    private fun sendNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 创建通知渠道（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "每日菜单",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "每天早上推送今日菜单推荐"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // 创建打开应用的 Intent
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // 构建通知
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    companion object {
        const val CHANNEL_ID = "daily_menu_channel"
        const val NOTIFICATION_ID = 1
        const val WORK_NAME = "daily_menu_work"
        
        // 设置每日8点执行任务
        fun schedule(context: Context) {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            
            // 设置时间为8:00
            dueDate.set(Calendar.HOUR_OF_DAY, 8)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            
            // 如果今天8点已过，设置为明天8点
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            
            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyMenuWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest
            )
        }
        
        // 取消任务
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

// 开机启动接收器
class DailyMenuReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DailyMenuWorker.schedule(context)
        }
    }
}