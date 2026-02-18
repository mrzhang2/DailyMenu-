package com.dailymenu.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dailymenu.R
import com.dailymenu.data.database.UserDao
import com.dailymenu.data.model.MemberLevel
import com.dailymenu.data.model.User
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val userDao: UserDao
) {
    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private const val SCOPE = "snsapi_userinfo"
        private const val STATE = "dailymenu_wechat_login"
    }

    private lateinit var wxApi: IWXAPI
    private val _appId: String

    init {
        _appId = context.getString(R.string.wechat_app_id)
        registerWxApi()
    }

    private fun registerWxApi() {
        if (_appId != "YOUR_WECHAT_APP_ID") {
            wxApi = WXAPIFactory.createWXAPI(context, _appId, true)
            wxApi.registerApp(_appId)
        }
    }

    fun getWxApi(): IWXAPI = wxApi

    fun isWeChatInstalled(): Boolean {
        return if (::wxApi.isInitialized) {
            wxApi.isWXAppInstalled
        } else {
            false
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY] != null
    }

    val currentUser: Flow<User?> = userDao.getCurrentUser()

    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }.first()
    }

    fun loginWithWeChat(): Boolean {
        return if (::wxApi.isInitialized && wxApi.isWXAppInstalled) {
            val req = SendAuth.Req().apply {
                scope = SCOPE
                state = STATE
            }
            wxApi.sendReq(req)
            true
        } else {
            false
        }
    }

    suspend fun handleWeChatLogin(code: String): Result<User> {
        return try {
            // TODO: 在实际项目中，这里应该调用后端 API 用 code 换取 access_token 和用户信息
            // 目前使用模拟数据进行演示
            val mockUser = createMockUser(code)
            
            // 保存用户信息到数据库
            userDao.insertUser(mockUser)
            
            // 保存登录 token
            dataStore.edit { preferences ->
                preferences[AUTH_TOKEN_KEY] = "mock_token_${System.currentTimeMillis()}"
            }
            
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun mockLogin(): Result<User> {
        return try {
            val mockUser = createMockUser("demo_${System.currentTimeMillis()}")
            
            // 保存用户信息到数据库
            userDao.insertUser(mockUser)
            
            // 保存登录 token
            dataStore.edit { preferences ->
                preferences[AUTH_TOKEN_KEY] = "mock_token_${System.currentTimeMillis()}"
            }
            
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        // 清除 token
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
        // 清除用户数据
        userDao.deleteAllUsers()
    }

    private fun createMockUser(code: String): User {
        val timestamp = System.currentTimeMillis()
        return User(
            id = "user_$timestamp",
            wechatOpenId = "wx_openid_$code",
            nickname = "微信用户${(1000..9999).random()}",
            avatarUrl = "https://via.placeholder.com/150",
            gender = (0..2).random(),
            memberLevel = MemberLevel.FREE,
            createTime = timestamp,
            lastLoginTime = timestamp
        )
    }
}
