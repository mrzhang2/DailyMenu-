package com.dailymenu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.TextPrimary
import com.dailymenu.ui.theme.TextSecondary

@Composable
fun BudgetSettingDialog(
    currentBudget: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var selectedBudget by remember { mutableFloatStateOf(currentBudget) }
    
    // 根据预算金额返回建议文字
    val suggestionText = when {
        selectedBudget < 30 -> "预算较低，建议选择简单的家常菜"
        selectedBudget < 60 -> "预算适中，可以尝试多样化的菜品"
        selectedBudget < 100 -> "预算充足，可以品尝一些特色菜肴"
        else -> "预算丰富，尽情享受美食吧！"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "设置每日预算",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 预算滑动选择器
                BudgetSlider(
                    budget = selectedBudget,
                    onBudgetChange = { selectedBudget = it }
                )
                
                // 预算建议文字
                Text(
                    text = suggestionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedBudget) }
            ) {
                Text(
                    text = "确定",
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "取消",
                    color = TextSecondary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BudgetSettingDialogPreview() {
    BudgetSettingDialog(
        currentBudget = 50f,
        onDismiss = {},
        onConfirm = {}
    )
}
