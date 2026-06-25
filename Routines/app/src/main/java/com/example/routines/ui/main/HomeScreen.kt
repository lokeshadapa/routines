package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskSummary
import com.example.routines.theme.*
import com.example.routines.DAY_SHORT
import com.example.routines.formatDuration
import com.example.routines.formatScheduleDays
import com.example.routines.ui.viewmodel.RoutineViewModel
import java.util.Calendar

private fun timeGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 5  -> "Good night"
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        hour < 21 -> "Good evening"
        else      -> "Good night"
    }
}

val QUOTES = listOf(
    "The secret of your future is hidden in your daily routine.",
    "Motivation is what gets you started. Habit is what keeps you going.",
    "Quality is not an act, it is a habit.",
    "We are what we repeatedly do.",
    "Small daily improvements are the key to staggering long-term results.",
    "Success is the sum of small efforts repeated day in and day out.",
    "A good routine is the foundation of a productive life.",
    "Discipline is the bridge between goals and accomplishment.",
    "Excellence is not a singular act but a habit.",
    "Routine is the master of all skills.",
    "It's not what we do once in a while that shapes our lives, but what we do consistently.",
    "Consistency is the hallmark of the unimaginative.",
    "Make each day your masterpiece.",
    "Build good habits and they will build you.",
    "The chains of habit are too light to be felt until they are too heavy to be broken.",
    "Your daily routine is your future self's foundation.",
    "First forget inspiration. Habit is more dependable.",
    "Champions don't do extraordinary things. They do ordinary things extraordinarily well.",
    "Successful people are simply those with successful habits.",
    "You'll never change your life until you change something you do daily."
)

@Composable
fun HomeScreen(
    viewModel: RoutineViewModel,
    onCreateRoutineClick: () -> Unit,
    onEditRoutineClick: (Long) -> Unit,
    onPlayRoutineClick: (Long) -> Unit,
    onExploreClick: () -> Unit,
    onUseTemplate: (name: String, tasks: List<DraftTask>) -> Unit
) {
    val routines by viewModel.allRoutines.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 180.dp)
        ) {
            item {
                // Header — mirrors Explore page structure
                Column(
                    modifier = Modifier.padding(top = 30.dp, start = 24.dp, end = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Routines",
                            style = MaterialTheme.typography.headlineLarge,
                            color = NearBlack
                        )
                        val addSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(BurntOrange, CircleShape)
                                .clickable(
                                    interactionSource = addSource,
                                    indication = null,
                                    onClick = onCreateRoutineClick
                                )
                                .bouncyPress(addSource),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Create Routine",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(Modifier.width(48.dp).height(2.dp).background(NearBlack))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = timeGreeting(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubText
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (routines.isEmpty()) {
                item {
                    EmptyStateContent(
                        onUseTemplate = onUseTemplate,
                        onExploreClick = onExploreClick
                    )
                }
            } else {

                items(routines, key = { it.id }) { routine ->
                    val summary by viewModel.getTaskSummary(routine.id)
                        .collectAsStateWithLifecycle(initialValue = TaskSummary(0, 0, null))
                    RoutineCard(
                        routine = routine,
                        summary = summary,
                        onEditClick = { onEditRoutineClick(routine.id) },
                        onPlayClick = { onPlayRoutineClick(routine.id) },
                        modifier = Modifier.padding(
                            top = 10.dp, start = 20.dp, end = 20.dp
                        )
                    )
                }
            }
        }

    }
}

@Composable
fun RoutineCard(
    routine: RoutineEntity,
    summary: TaskSummary,
    onEditClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasSchedule = routine.daysOfWeek != 0
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Accent bar for scheduled routines
            if (hasSchedule) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .defaultMinSize(minHeight = 72.dp)
                        .fillMaxHeight()
                        .background(
                            BurntOrange,
                            RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEditClick)
                    .padding(
                        start = if (hasSchedule) 14.dp else 18.dp,
                        end = 18.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        val icon = routine.icon.ifEmpty { summary.firstTaskIcon }
                        if (!icon.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(BurntOrangeLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                        }
                        Text(
                            text = routine.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = NearBlack
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BurntOrange, CircleShape)
                            .clickable(onClick = onPlayClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val taskWord = if (summary.taskCount == 1) "task" else "tasks"
                    listOf(
                        formatDuration(summary.totalDuration),
                        "${summary.taskCount} $taskWord"
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
                    if (hasSchedule) {
                        Box(
                            modifier = Modifier
                                .background(NeutralPill, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formatScheduleDays(routine.daysOfWeek),
                                style = MaterialTheme.typography.labelSmall,
                                color = SubText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateContent(
    onUseTemplate: (String, List<DraftTask>) -> Unit,
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ghost card stack
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(136.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(64.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NearBlack.copy(alpha = 0.05f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {}
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(64.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-12).dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NearBlack.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {}
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(Modifier.size(38.dp).background(NeutralPill, CircleShape))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(Modifier.fillMaxWidth(0.55f).height(12.dp).background(NeutralPill, RoundedCornerShape(6.dp)))
                        Spacer(Modifier.height(7.dp))
                        Box(Modifier.fillMaxWidth(0.30f).height(9.dp).background(BurntOrangeLight, RoundedCornerShape(5.dp)))
                    }
                    Box(Modifier.size(36.dp).background(NeutralPill, CircleShape))
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "No routines yet",
            style = MaterialTheme.typography.headlineMedium,
            color = NearBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Build your first routine or start with a template below.",
            style = MaterialTheme.typography.bodyMedium,
            color = SubText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Section label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SUGGESTED",
                style = MaterialTheme.typography.labelLarge,
                color = NearBlack
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2 template suggestion cards
        ROUTINE_TEMPLATES.take(2).forEach { template ->
            val totalSecs = template.tasks.sumOf { it.durationSeconds }
            val taskWord = if (template.tasks.size == 1) "task" else "tasks"
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(BurntOrangeLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(template.emoji, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                        Text(
                            text = "${formatDuration(totalSecs)} · ${template.tasks.size} $taskWord".uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = SubText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(BurntOrange, CircleShape)
                            .clickable { onUseTemplate(template.name, template.tasks) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Use template",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Explore More button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onExploreClick() }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore More Templates",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BurntOrange
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = BurntOrange,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
