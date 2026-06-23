package com.example.routines.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = BurntOrange,
    onPrimary        = Color.White,
    primaryContainer = BurntOrangeLight,
    onPrimaryContainer = NearBlack,
    background       = WarmWhite,
    onBackground     = NearBlack,
    surface          = CardWhite,
    onSurface        = NearBlack,
    surfaceVariant   = BurntOrangeLight,
    onSurfaceVariant = SubText,
    outline          = BorderLight,
)

@Composable
fun RoutinesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
