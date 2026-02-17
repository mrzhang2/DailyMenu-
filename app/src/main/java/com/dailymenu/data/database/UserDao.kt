package com.dailymenu.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dailymenu.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("UPDATE users SET lastLoginTime = :timestamp WHERE id = :userId")
    suspend fun updateLastLoginTime(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM users WHERE wechatOpenId = :openId")
    suspend fun getUserByWechatOpenId(openId: String): User?

    @Query("UPDATE users SET streakDays = :days WHERE id = :userId")
    suspend fun updateStreakDays(userId: String, days: Int)

    @Query("UPDATE users SET totalCookingDays = totalCookingDays + 1 WHERE id = :userId")
    suspend fun incrementTotalCookingDays(userId: String)
}
