package com.dailymenu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dailymenu.data.model.Comment
import com.dailymenu.data.model.LearningProgress
import com.dailymenu.data.model.Notification
import com.dailymenu.data.model.Recipe
import com.dailymenu.data.model.StepImage
import com.dailymenu.data.model.User
import com.dailymenu.data.model.Work

@Database(
    entities = [Recipe::class, StepImage::class, User::class, Comment::class, Work::class, LearningProgress::class, Notification::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun workDao(): WorkDao
    abstract fun learningProgressDao(): LearningProgressDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dailymenu_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}