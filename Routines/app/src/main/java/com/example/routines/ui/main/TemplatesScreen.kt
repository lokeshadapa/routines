package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routines.theme.*

data class RoutineTemplate(
    val name: String,
    val description: String,
    val emoji: String,
    val tasks: List<DraftTask>
)

val ROUTINE_TEMPLATES = listOf(
    RoutineTemplate(
        name = "Morning Workout",
        description = "Start the day with energy",
        emoji = "🏋️",
        tasks = listOf(
            DraftTask("Warm Up", 300, "🔥"),
            DraftTask("Squats", 480, "💪"),
            DraftTask("Push-Ups", 300, "🤸"),
            DraftTask("Cool Down", 300, "❄️")
        )
    ),
    RoutineTemplate(
        name = "Evening Wind Down",
        description = "Ease your way into sleep",
        emoji = "🌙",
        tasks = listOf(
            DraftTask("Stretching", 600, "🧘"),
            DraftTask("Journaling", 600, "📔"),
            DraftTask("Meditation", 300, "🌊")
        )
    ),
    RoutineTemplate(
        name = "Deep Work Block",
        description = "Focused, distraction-free work",
        emoji = "💻",
        tasks = listOf(
            DraftTask("Focus Session", 1500, "🎯"),
            DraftTask("Short Break", 300, "☕"),
            DraftTask("Focus Session", 1500, "🎯")
        )
    ),
    RoutineTemplate(
        name = "Mindful Morning",
        description = "Center yourself before the day",
        emoji = "🧘",
        tasks = listOf(
            DraftTask("Meditation", 600, "🌅"),
            DraftTask("Gratitude Journal", 300, "📝"),
            DraftTask("Light Walk", 900, "🚶")
        )
    ),
    RoutineTemplate(
        name = "Fitness Blitz",
        description = "High intensity, short time",
        emoji = "🔥",
        tasks = listOf(
            DraftTask("Jumping Jacks", 180, "⚡"),
            DraftTask("Burpees", 180, "💥"),
            DraftTask("Mountain Climbers", 180, "🏔️"),
            DraftTask("Plank Hold", 120, "🧱"),
            DraftTask("Rest", 120, "😮‍💨")
        )
    ),
    RoutineTemplate(
        name = "Study Session",
        description = "Learn smarter, not harder",
        emoji = "📚",
        tasks = listOf(
            DraftTask("Review Notes", 600, "📖"),
            DraftTask("Active Study", 1500, "✏️"),
            DraftTask("Break", 300, "☕"),
            DraftTask("Practice Problems", 1200, "🧩")
        )
    )
)

private fun formatTemplateDuration(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        m > 0 -> "${m} min"
        else -> "${totalSeconds}s"
    }
}

@Composable
fun TemplatesScreen(
    onUseTemplate: (name: String, tasks: List<DraftTask>) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 160.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 30.dp, start = 24.dp, end = 24.dp)) {
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.headlineLarge,
                        color = NearBlack
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.width(48.dp).height(2.dp).background(NearBlack))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ready-made routines to get you started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubText
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }

            items(ROUTINE_TEMPLATES) { template ->
                val totalSecs = template.tasks.sumOf { it.durationSeconds }
                TemplateCard(
                    template = template,
                    totalSecs = totalSecs,
                    onUseThis = { onUseTemplate(template.name, template.tasks) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: RoutineTemplate,
    totalSecs: Int,
    onUseThis: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BurntOrangeLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(template.emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = NearBlack
                    )
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SubText
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(BurntOrange, CircleShape)
                        .clickable { onUseThis() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Use template",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val taskWord = if (template.tasks.size == 1) "task" else "tasks"
                listOf(
                    formatTemplateDuration(totalSecs),
                    "${template.tasks.size} $taskWord"
                ).forEach { label ->
                    Box(
                        modifier = Modifier
                            .background(BurntOrangeLight, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = BurntOrange
                        )
                    }
                }
            }
        }
    }
}
