package com.example.movielookup.ui.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.example.movielookup.R

// GANTI DENGAN FONT-MU SENDIRI (contoh: Poppins)
val MovieFont = FontFamily(
    Font(R.font.poppinsregular),
    Font(R.font.poppinsmedium),
    Font(R.font.poppinsbold)
)

private val DarkColors = darkColors(
    primary = Color(0xFF1A1E29),
    primaryVariant = Color(0xFF0F1218),
    secondary = Color(0xFFE50914), // Netflix red
    background = Color(0xFF12141B),
    surface = Color(0xFF1C1F2A),
    onPrimary = Color.White,
    onBackground = Color.White
)

@Composable
fun MovieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColors,
        typography = Typography(defaultFontFamily = MovieFont),
        shapes = Shapes(),
        content = content
    )
}
