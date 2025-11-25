package com.example.movielookup.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.movielookup.R

val Poppins = FontFamily(
    Font(R.font.poppinsregular, FontWeight.Normal),
    Font(R.font.poppinsmedium, FontWeight.Medium),
    Font(R.font.poppinssemibold, FontWeight.SemiBold)
)

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 12.sp
    )
)
