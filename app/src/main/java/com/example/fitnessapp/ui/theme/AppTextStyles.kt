package com.example.fitnessapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppTextStyles {
    @Composable // большие кнопки = текст, в меню
    fun menuButton(): TextStyle = MaterialTheme.typography.bodyLarge

    @Composable // маленькие кнопки - при большом количестве текста /недостатке места
    fun smallMenuButton(): TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 16.sp
    )

    @Composable
    fun regularInfo(): TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal
    )

    // Для небольшого текста: описания заданий, подсказки
    @Composable
    fun bodySmall(): TextStyle = MaterialTheme.typography.bodySmall

    // Для системных плашек, тегов, статусов (LOCKED, тип упражнения и т.д.)
    @Composable
    fun badge(): TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )

    // Для описания миссий/заданий - чуть крупнее чем badge, но меньше regularInfo
    @Composable
    fun missionBody(): TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 25.sp,
        fontWeight = FontWeight.Normal
    )

    // Для подписей под графиком, дат, мелких меток
    @Composable
    fun caption(): TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    )
}
