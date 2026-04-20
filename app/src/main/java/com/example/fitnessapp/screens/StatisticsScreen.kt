package com.example.fitnessapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.entities.ExerciseEntity
import com.example.fitnessapp.entities.ExerciseType
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.view_models.StatisticsViewModel
import java.text.SimpleDateFormat
import java.util.*

private val categoryColors = mapOf(
    ExerciseType.CARDIO to Color(0xFFE53935),
    ExerciseType.LOWER_BODY to Color(0xFF1E88E5),
    ExerciseType.UPPER_BODY to Color(0xFF43A047),
    ExerciseType.CORE to Color(0xFFFB8C00),
    ExerciseType.BALANCE to Color(0xFFFFF41E),
    ExerciseType.MOBILITY to Color(0xFF8E24AA)
)

private val categoryLabels = mapOf(
    ExerciseType.CARDIO to "Cardio",
    ExerciseType.LOWER_BODY to "Lower Body",
    ExerciseType.UPPER_BODY to "Upper Body",
    ExerciseType.CORE to "Core",
    ExerciseType.BALANCE to "Balance",
    ExerciseType.MOBILITY to "Mobility"
)

private val exerciseNames = mapOf(
    ExerciseType.CARDIO to "Peter's Warmup",
    ExerciseType.LOWER_BODY to "Thunder Kick",
    ExerciseType.UPPER_BODY to "Wall Crawler",
    ExerciseType.CORE to "Spider Plank",
    ExerciseType.BALANCE to "Leap of Faith",
    ExerciseType.MOBILITY to "Gwen's Rest"
)

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
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
                StatSummaryItem("Missions", "$streak") // заглушка
                StatSummaryItem("Streak", "$streak")
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
                0 -> CategoryPieChart(categoryTotals)
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
private fun CategoryPieChart(categoryTotals: Map<ExerciseType, Int>) {
    val total = categoryTotals.values.sum().toFloat()
    val nonZero = categoryTotals.filter { it.value > 0 }

    if (total == 0f) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_workouts), color = Color.White)
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 48.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val topLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(radius * 2, radius * 2)
                var startAngle = -90f
                nonZero.forEach { (type, count) ->
                    val sweep = (count / total) * 360f
                    drawArc(
                        color = categoryColors[type] ?: Color.Gray,
                        startAngle = startAngle,
                        sweepAngle = sweep - 1f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweep
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xB3161616), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            nonZero.forEach { (type, count) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(categoryColors[type] ?: Color.Gray, CircleShape)
                    )
                    Text(
                        exerciseNames[type] ?: "",
                        color = Color(0xFFE0E0E0),
                        style = AppTextStyles.missionBody(),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "$count",
                        color = categoryColors[type] ?: Color.White,
                        style = AppTextStyles.missionBody(),
                        textAlign = TextAlign.End
                    )
                }
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                )
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
                        Text(
                            "${exerciseNames[exercise.exerciseType]} — ${exercise.count}",
                            color = Color.White,
                            style = AppTextStyles.missionBody()
                        )
                        Text(
                            categoryLabels[exercise.exerciseType] ?: "",
                            color = categoryColors[exercise.exerciseType] ?: Color.Gray,
                            style = AppTextStyles.badge()
                        )
                    }
                    Button(
                        onClick = { onDelete(exercise) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(stringResource(R.string.delete_workout), style = AppTextStyles.badge())
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
