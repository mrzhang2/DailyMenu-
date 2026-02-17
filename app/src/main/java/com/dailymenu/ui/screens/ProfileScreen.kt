package com.dailymenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dailymenu.data.model.MemberLevel
import com.dailymenu.data.model.User
import com.dailymenu.ui.components.MenuItem
import com.dailymenu.ui.components.StatCard
import com.dailymenu.ui.theme.*
import com.dailymenu.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "我的",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = TextPrimary
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoggedIn && currentUser != null) {
                UserProfileContent(
                    user = currentUser!!,
                    onNavigateToFavorites = onNavigateToFavorites,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                GuestContent(
                    onNavigateToLogin = onNavigateToLogin,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun UserProfileContent(
    user: User,
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UserInfoCard(user = user)
        StatsRow(user = user)
        FunctionMenuCard(
            onNavigateToFavorites = onNavigateToFavorites
        )
        OtherMenuCard()
    }
}

@Composable
fun UserInfoCard(
    user: User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "头像",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "头像",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 昵称
            Text(
                text = user.nickname,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 会员等级标签
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (user.memberLevel) {
                    MemberLevel.FREE -> TextSecondary.copy(alpha = 0.1f)
                    MemberLevel.VIP -> SecondaryYellow.copy(alpha = 0.2f)
                    MemberLevel.SVIP -> PrimaryOrange.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = user.memberLevel.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (user.memberLevel) {
                        MemberLevel.FREE -> TextSecondary
                        MemberLevel.VIP -> SecondaryYellowDark
                        MemberLevel.SVIP -> PrimaryOrangeDark
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // 简介
            user.bio?.let { bio ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StatsRow(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = Icons.Default.Favorite,
            label = "收藏",
            value = user.favoriteCount.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.MenuBook,
            label = "已学",
            value = user.learnedCount.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.PhotoCamera,
            label = "作品",
            value = user.postCount.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.LocalFireDepartment,
            label = "打卡",
            value = user.streakDays.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FunctionMenuCard(
    onNavigateToFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MenuItem(
                icon = Icons.Default.Favorite,
                title = "我的收藏",
                subtitle = "管理您收藏的菜谱",
                onClick = onNavigateToFavorites
            )
            MenuItem(
                icon = Icons.Default.History,
                title = "浏览历史",
                onClick = { /* TODO */ }
            )
            MenuItem(
                icon = Icons.Default.PhotoLibrary,
                title = "我的作品",
                onClick = { /* TODO */ }
            )
            MenuItem(
                icon = Icons.Default.Download,
                title = "下载管理",
                onClick = { /* TODO */ },
                showDivider = false
            )
        }
    }
}

@Composable
fun OtherMenuCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MenuItem(
                icon = Icons.Default.Notifications,
                title = "消息通知",
                onClick = { /* TODO */ }
            )
            MenuItem(
                icon = Icons.Default.Help,
                title = "帮助与反馈",
                onClick = { /* TODO */ },
                showDivider = false
            )
        }
    }
}

@Composable
fun GuestContent(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = PrimaryOrange.copy(alpha = 0.5f),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "登录后享受更多功能",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "收藏菜谱、记录学习进度、分享作品",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange
            )
        ) {
            Text(
                text = "立即登录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
