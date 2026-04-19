package com.example.fitnessapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.entities.ExerciseEntity
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.view_models.StatisticsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val totalSquats by viewModel.totalSquats.collectAsState()
    val menuButtonTextStyle = AppTextStyles.menuButton()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { navController.navigateUp() }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.return_button), style = menuButtonTextStyle)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xB3161616))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatSummaryItem(stringResource(R.string.total_squats_txt), "$totalSquats")
                StatSummaryItem(stringResource(R.string.total_workouts_label), "${exercises.size}")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xB3161616),
            contentColor = Color(0xFFE53935)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text(
                    stringResource(R.string.tab_chart),
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (selectedTab == 0) Color(0xFFE53935) else Color.White
                )
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text(
                    stringResource(R.string.tab_table),
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (selectedTab == 1) Color(0xFFE53935) else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (exercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_workouts), color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        } else {
            when (selectedTab) {
                0 -> SquatBarChart(exercises)
                1 -> ExerciseTable(exercises, viewModel::deleteExercise, viewModel::deleteAllExercises)
            }
        }
    }
}

@Composable
private fun StatSummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color(0xFFE53935), fontSize = 28.sp, style = AppTextStyles.menuButton())
        Text(label, color = Color(0xFFE0E0E0), fontSize = 13.sp)
    }
}

@Composable
private fun SquatBarChart(exercises: List<ExerciseEntity>) {
    val fmt = SimpleDateFormat("dd.MM", Locale.getDefault())
    val byDay: List<Map.Entry<String, List<ExerciseEntity>>> = exercises
        .groupBy { e -> fmt.format(Date(e.timestamp)) }
        .entries.toList().takeLast(7)
    val maxVal = byDay.maxOfOrNull { entry -> entry.value.sumOf { e: ExerciseEntity -> e.count } }?.toFloat() ?: 1f
    val barColor = Color(0xFFE53935)
    val labelColor = Color(0xFFE0E0E0)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.chart_last_7_days),
            color = Color.White,
            style = AppTextStyles.menuButton(),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xB3161616), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            val barCount = byDay.size
            if (barCount == 0) return@Canvas
            val barWidth = (size.width / barCount) * 0.6f
            val gap = (size.width / barCount) * 0.4f
            byDay.forEachIndexed { i, entry ->
                val total = entry.value.sumOf { e: ExerciseEntity -> e.count }.toFloat()
                val barH = (total / maxVal) * (size.height - 24.dp.toPx())
                val x = i * (barWidth + gap) + gap / 2f
                val y = size.height - barH - 20.dp.toPx()
                drawRect(color = barColor, topLeft = Offset(x, y), size = Size(barWidth, barH))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            byDay.forEach { entry ->
                Text(entry.key, color = labelColor, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ExerciseTable(
    exercises: List<ExerciseEntity>,
    onDelete: (ExerciseEntity) -> Unit,
    onDeleteAll: () -> Unit
) {
    val menuButtonTextStyle = AppTextStyles.menuButton()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Button(
                onClick = onDeleteAll,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_all_workouts), style = menuButtonTextStyle)
            }
        }
        items(exercises) { exercise ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xB3161616))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(formatDate(exercise.timestamp), color = Color(0xFFE0E0E0), fontSize = 13.sp)
                        Text("${exercise.count} приседаний", color = Color.White, style = AppTextStyles.menuButton())
                    }
                    TextButton(onClick = { onDelete(exercise) }) {
                        Text(stringResource(R.string.delete_workout), color = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
