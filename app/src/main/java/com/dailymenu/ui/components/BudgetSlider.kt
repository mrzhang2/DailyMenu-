package com.dailymenu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.PrimaryOrangeLight
import com.dailymenu.ui.theme.SurfaceWhite
import com.dailymenu.ui.theme.TextPrimary
import com.dailymenu.ui.theme.TextSecondary

@Composable
fun BudgetSlider(
    budget: Float,
    onBudgetChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float> = 20f..200f
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 显示当前预算
            Text(
                text = "${budget.toInt()}元",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryOrange
            )
            
            Text(
                text = "元/天",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Slider
            Slider(
                value = budget,
                onValueChange = onBudgetChange,
                valueRange = range,
                steps = 17, // 20-200, step 10
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryOrange,
                    activeTrackColor = PrimaryOrange,
                    inactiveTrackColor = PrimaryOrangeLight.copy(alpha = 0.3f)
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 左右标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "节省",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "丰盛",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetSliderPreview() {
    BudgetSlider(
        budget = 50f,
        onBudgetChange = {}
    )
}
