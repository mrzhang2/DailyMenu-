package com.dailymenu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.PrimaryOrangeLight
import com.dailymenu.ui.theme.SurfaceWhite
import com.dailymenu.ui.theme.TextPrimary

@Composable
fun BudgetCard(
    budget: Float,
    spent: Float,
    onClick: () -> Unit
) {
    val progress = if (budget > 0) (spent / budget).coerceIn(0f, 1f) else 0f
    val isOverBudget = spent > budget
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryOrange, PrimaryOrangeLight)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 左侧内容
                Column {
                    // 顶部：今日预算文字和图标
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "预算",
                            tint = SurfaceWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "今日预算",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SurfaceWhite.copy(alpha = 0.9f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 中间：预算金额
                    Text(
                        text = "${budget.toInt()}元",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 底部：预计花费
                    Text(
                        text = "预计花费 ${spent.toInt()}元",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SurfaceWhite.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 进度条
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isOverBudget) {
                            androidx.compose.ui.graphics.Color.Red
                        } else {
                            SurfaceWhite
                        },
                        trackColor = SurfaceWhite.copy(alpha = 0.3f)
                    )
                }
                
                // 右侧设置按钮
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置预算",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetCardPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        BudgetCard(
            budget = 50f,
            spent = 35f,
            onClick = {}
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BudgetCard(
            budget = 30f,
            spent = 45f,
            onClick = {}
        )
    }
}
