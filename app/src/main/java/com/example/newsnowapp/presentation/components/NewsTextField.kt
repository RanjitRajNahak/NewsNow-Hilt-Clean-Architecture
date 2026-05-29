package com.example.app1.ui.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun NewsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = FontFamily.SansSerif
            )
        },
        modifier = Modifier.fillMaxWidth(),
        // Modern Pill Shape
        shape = RoundedCornerShape(50.dp),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1565C0),
            unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
            focusedLabelColor = Color(0xFF1565C0),
            unfocusedLabelColor = Color(0xFF1565C0),
            // High-contrast white background inside the glass card
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        singleLine = true
    )
}