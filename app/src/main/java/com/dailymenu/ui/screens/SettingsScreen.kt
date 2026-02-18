package com.dailymenu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.components.SettingsActionItem
import com.dailymenu.ui.components.SettingsSection
import com.dailymenu.ui.components.SettingsSwitchItem
import com.dailymenu.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onManualWeatherClick: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var cacheSize by remember { mutableStateOf("12.5 MB") }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(8) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCream
                )
            )
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection(title = "账号管理") {
                SettingsActionItem(
                    icon = Icons.Default.Chat,
                    title = "微信绑定",
                    subtitle = "未绑定",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                SettingsActionItem(
                    icon = Icons.Default.Phone,
                    title = "手机绑定",
                    subtitle = "未绑定",
                    onClick = { }
                )
            }

            SettingsSection(title = "通知设置") {
                var pushEnabled by remember { mutableStateOf(true) }
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "推送开关",
                    subtitle = "接收每日菜单推送通知",
                    checked = pushEnabled,
                    onCheckedChange = { pushEnabled = it }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                SettingsActionItem(
                    icon = Icons.Default.Schedule,
                    title = "推送时间",
                    subtitle = "每天 ${selectedHour}:00",
                    onClick = { showTimePickerDialog = true }
                )
            }

            SettingsSection(title = "播放设置") {
                var videoQuality by remember { mutableStateOf("自动") }
                SettingsActionItem(
                    icon = Icons.Default.HighQuality,
                    title = "视频质量",
                    subtitle = videoQuality,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                var wifiDownloadOnly by remember { mutableStateOf(false) }
                SettingsSwitchItem(
                    icon = Icons.Default.Wifi,
                    title = "WiFi下载",
                    subtitle = "仅在WiFi环境下下载视频",
                    checked = wifiDownloadOnly,
                    onCheckedChange = { wifiDownloadOnly = it }
                )
            }

            SettingsSection(title = "隐私设置") {
                SettingsActionItem(
                    icon = Icons.Default.Block,
                    title = "黑名单",
                    subtitle = "管理黑名单列表",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                SettingsActionItem(
                    icon = Icons.Default.Security,
                    title = "授权管理",
                    subtitle = "管理应用权限",
                    onClick = { }
                )
            }

            SettingsSection(title = "通用") {
                SettingsActionItem(
                    icon = Icons.Default.DeleteSweep,
                    title = "清理缓存",
                    subtitle = "当前缓存: $cacheSize",
                    onClick = { cacheSize = "0 MB" }
                )
            }

            SettingsSection(title = "关于我们") {
                SettingsActionItem(
                    icon = Icons.Default.Info,
                    title = "版本号",
                    subtitle = "v1.0.0",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                SettingsActionItem(
                    icon = Icons.Default.Description,
                    title = "用户协议",
                    subtitle = "查看用户服务协议",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BackgroundCream)
                SettingsActionItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "隐私政策",
                    subtitle = "查看隐私保护政策",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "退出登录",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出当前账号吗？") },
            confirmButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("确定", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            title = { Text("选择推送时间") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        showTimePickerDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
