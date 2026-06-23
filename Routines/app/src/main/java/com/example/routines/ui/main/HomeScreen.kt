package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskSummary
import com.example.routines.theme.*
import com.example.routines.ui.viewmodel.RoutineViewModel
import kotlinx.coroutines.delay

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

private fun formatDuration(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m} min"
        h > 0           -> "${h}h"
        m > 0           -> "${m} min"
        else            -> "${totalSeconds}s"
    }
}

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

    val selectedQuote = remember { QUOTES.random() }
    var displayedQuote by remember { mutableStateOf("") }

    LaunchedEffect(selectedQuote) {
        for (i in selectedQuote.indices) {
            displayedQuote = selectedQuote.substring(0, i + 1)
            delay(40)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 180.dp)
        ) {
            item {
                // Header
                Column(modifier = Modifier.padding(top = 30.dp, start = 24.dp, end = 24.dp)) {
                    Text(
                        text = "Routines",
                        style = MaterialTheme.typography.headlineLarge,
                        color = NearBlack
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(2.dp)
                            .background(NearBlack)
                    )
                }

                // Quote card
                Card(
                    modifier = Modifier
                        .padding(top = 22.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BurntOrangeLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "\"$displayedQuote\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF5A2A10),
                        minLines = 2,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
                    )
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
                item {
                    // Section header
                    Row(
                        modifier = Modifier
                            .padding(top = 28.dp, start = 24.dp, end = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "YOUR ROUTINES",
                                style = MaterialTheme.typography.labelLarge,
                                color = NearBlack
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(thickness = 2.dp, color = NearBlack)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        val badgeEmpty = routines.isEmpty()
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(
                                    if (badgeEmpty) Color(0xFFE8D0C0) else BurntOrange,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${routines.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (badgeEmpty) SubText else Color.White,
                                style = androidx.compose.ui.text.TextStyle(
                                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                                    lineHeight = 11.sp
                                )
                            )
                        }
                    }
                }

                items(routines, key = { it.id }) { routine ->
                    val summary by viewModel.getTaskSummary(routine.id)
                        .collectAsStateWithLifecycle(initialValue = TaskSummary(0, 0))
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

        ExtendedFloatingActionButton(
            text = {
                Text(
                    "Create Routine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            },
            icon = {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            },
            onClick = onCreateRoutineClick,
            shape = RoundedCornerShape(28.dp),
            containerColor = BurntOrange,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(bottom = 104.dp, end = 20.dp)
        )
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onEditClick)
            ) {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = NearBlack
                )
                Spacer(modifier = Modifier.height(3.dp))
                val taskWord = if (summary.taskCount == 1) "task" else "tasks"
                val label = "${formatDuration(summary.totalDuration)} · ${summary.taskCount} $taskWord"
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText
                )
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(BurntOrange, CircleShape)
                    .clickable(onClick = onPlayClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Start",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
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
