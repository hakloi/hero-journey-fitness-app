package com.example.fitnessapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    bodyLarge = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    titleLarge = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Bold, fontSize = 30.sp),
    headlineLarge = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Bold, fontSize = 50.sp),
    bodySmall = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    labelSmall = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Normal, fontSize = 11.sp),
    bodyMedium = TextStyle(fontFamily = MyAppFont, fontWeight = FontWeight.Normal, fontSize = 18.sp)
)