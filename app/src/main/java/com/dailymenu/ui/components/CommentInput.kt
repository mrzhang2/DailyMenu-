package com.dailymenu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailymenu.ui.theme.PrimaryOrange
import com.dailymenu.ui.theme.SurfaceWhite

@Composable
fun CommentInput(
    onSubmit: (String) -> Unit,
    placeholder: String = "说点什么...",
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceWhite,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = com.dailymenu.ui.theme.TextSecondary
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryOrange,
                    unfocusedBorderColor = PrimaryOrange.copy(alpha = 0.3f),
                    focusedContainerColor = PrimaryOrange.copy(alpha = 0.05f),
                    unfocusedContainerColor = PrimaryOrange.copy(alpha = 0.05f)
                ),
                maxLines = 3
            )

            FilledIconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSubmit(text.trim())
                        text = ""
                    }
                },
                enabled = text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = PrimaryOrange,
                    disabledContainerColor = PrimaryOrange.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "发送",
                    tint = SurfaceWhite
                )
            }
        }
    }
}
