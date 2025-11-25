package com.example.movielookup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val DarkColors = darkColorScheme(
    primary = PurpleDark,
    primaryContainer = PurpleVariantDark,
    background = BgDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSurface = TextDark,
    onBackground = TextDark
)

private val LightColors = lightColorScheme(
    primary = PurpleLight,
    primaryContainer = PurpleVariantLight,
    background = BgLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSurface = TextLight,
    onBackground = TextLight
)

@Composable
fun MovieLookupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // MATIKAN DINAMIC THEME
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = Shapes(
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(14.dp),
            large = RoundedCornerShape(20.dp)
        ),
        content = content
    )
}

