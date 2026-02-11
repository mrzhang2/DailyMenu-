package com.dailymenu

import android.app.Application
import com.dailymenu.worker.DailyMenuWorker

class DailyMenuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 设置每日菜单更新任务
        DailyMenuWorker.schedule(this)
    }
}