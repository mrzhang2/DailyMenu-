package com.dailymenu.wxapi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dailymenu.data.repository.AuthRepository
import com.dailymenu.ui.theme.DailyMenuTheme
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 微信登录回调 Activity
 * 必须放在 wxapi 包下，且类名必须为 WXEntryActivity
 */
@AndroidEntryPoint
class WXEntryActivity : ComponentActivity(), IWXAPIEventHandler {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DailyMenuTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 处理 Intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        try {
            if (::authRepository.isInitialized) {
                authRepository.getWxApi().handleIntent(intent, this)
            } else {
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    override fun onReq(req: BaseReq?) {
        // 微信发送的请求，一般不需要处理
    }

    override fun onResp(resp: BaseResp?) {
        when (resp?.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                // 用户同意授权
                if (resp is SendAuth.Resp) {
                    val code = resp.code
                    // 发送广播通知登录成功
                    sendLoginSuccessBroadcast(code)
                }
            }
            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                // 用户取消
                sendLoginCancelBroadcast()
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                // 用户拒绝授权
                sendLoginErrorBroadcast("用户拒绝授权")
            }
            else -> {
                // 其他错误
                sendLoginErrorBroadcast("授权失败: ${resp?.errStr}")
            }
        }
        finish()
    }

    private fun sendLoginSuccessBroadcast(code: String) {
        val intent = Intent(ACTION_WECHAT_LOGIN_SUCCESS).apply {
            putExtra(EXTRA_WECHAT_CODE, code)
        }
        sendBroadcast(intent)
    }

    private fun sendLoginCancelBroadcast() {
        val intent = Intent(ACTION_WECHAT_LOGIN_CANCEL)
        sendBroadcast(intent)
    }

    private fun sendLoginErrorBroadcast(message: String) {
        val intent = Intent(ACTION_WECHAT_LOGIN_ERROR).apply {
            putExtra(EXTRA_ERROR_MESSAGE, message)
        }
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_WECHAT_LOGIN_SUCCESS = "com.dailymenu.WECHAT_LOGIN_SUCCESS"
        const val ACTION_WECHAT_LOGIN_CANCEL = "com.dailymenu.WECHAT_LOGIN_CANCEL"
        const val ACTION_WECHAT_LOGIN_ERROR = "com.dailymenu.WECHAT_LOGIN_ERROR"
        const val EXTRA_WECHAT_CODE = "wechat_code"
        const val EXTRA_ERROR_MESSAGE = "error_message"
    }
}
