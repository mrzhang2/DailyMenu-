package com.dailymenu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.*

@Composable
fun ServingsCalculator(
    ingredients: List<String>,
    defaultServings: Int = 2,
    modifier: Modifier = Modifier
) {
    var currentServings by remember { mutableIntStateOf(defaultServings.coerceIn(1, 20)) }
    val ratio = currentServings.toFloat() / defaultServings.toFloat()
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 份量调节行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "食材用量",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                // 份量调节器
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 减按钮
                    IconButton(
                        onClick = { if (currentServings > 1) currentServings-- },
                        enabled = currentServings > 1,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentServings > 1) PrimaryOrangeLight.copy(alpha = 0.3f) else WarmCream
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "减少份量",
                            tint = if (currentServings > 1) PrimaryOrangeDark else TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // 份量显示
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${currentServings}人份",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    
                    // 加按钮
                    IconButton(
                        onClick = { if (currentServings < 20) currentServings++ },
                        enabled = currentServings < 20,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentServings < 20) PrimaryOrangeLight.copy(alpha = 0.3f) else WarmCream
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "增加份量",
                            tint = if (currentServings < 20) PrimaryOrangeDark else TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = WarmCream
            )
            
            // 食材清单
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ingredients.forEachIndexed { index, ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = adjustIngredientAmount(ingredient, ratio),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 12.dp),
                            color = TextPrimary
                        )
                    }
                    if (index < ingredients.size - 1) {
                        Divider(
                            modifier = Modifier.padding(top = 8.dp),
                            color = WarmCream
                        )
                    }
                }
            }
        }
    }
}

/**
 * 简单调整食材用量
 * 尝试从食材字符串中提取数字并乘以比例
 */
private fun adjustIngredientAmount(ingredient: String, ratio: Float): String {
    // 正则表达式匹配数字（支持整数和小数）
    val numberRegex = "(\\d+\\.?\\d*)".toRegex()
    
    return numberRegex.replace(ingredient) { matchResult ->
        val originalNumber = matchResult.value.toFloatOrNull()
        if (originalNumber != null) {
            val adjustedNumber = originalNumber * ratio
            // 根据原数字是否为整数来决定输出格式
            if (originalNumber == originalNumber.toInt().toFloat()) {
                // 原数字是整数
                if (adjustedNumber == adjustedNumber.toInt().toFloat()) {
                    adjustedNumber.toInt().toString()
                } else {
                    String.format("%.1f", adjustedNumber)
                }
            } else {
                // 原数字是小数
                String.format("%.1f", adjustedNumber)
            }
        } else {
            matchResult.value
        }
    }
}
