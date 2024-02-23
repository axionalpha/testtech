package com.qudoos.myapp

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Define custom pastel colors
val PastelColors = lightColors(
    primary = Color(0xFF61714D),
    primaryVariant = Color(0xFFB6D3D2),
    secondary = Color(0xFFE4B5B6),
    secondaryVariant = Color(0xFFDBE4B5),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black

)

// Define custom typography
val CustomTypography = Typography(
    h1 = TextStyle(fontSize = 30.sp),
    h2 = TextStyle(fontSize = 24.sp),
    // Define other text styles as needed
)

// Composable function to apply custom theme
@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = PastelColors,
        typography = CustomTypography,
        content = content
    )
}
