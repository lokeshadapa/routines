package com.example.routines.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.routines.RoutineProgressNotification
import com.example.routines.formatTime
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskEntity
import com.example.routines.theme.*
import kotlinx.coroutines.delay

@Composable
fun RunningRoutineScreen(
    routine: RoutineEntity,
    tasks: List<TaskEntity>,
    onClose: () -> Unit
) {
    var currentTaskIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var totalElapsedSeconds by remember { mutableIntStateOf(0) }

    val currentTask = tasks.getOrNull(currentTaskIndex)

    var timeRemainingSeconds by remember(currentTask) {
        mutableIntStateOf(currentTask?.durationSeconds ?: 0)
    }

    // Post/update progress notification whenever the current task changes
    val context = LocalContext.current
    LaunchedEffect(currentTaskIndex, currentTask) {
        if (currentTask != null) {
            RoutineProgressNotification.show(
                context = context,
                routineName = routine.name,
                taskName = currentTask.name,
                taskIcon = currentTask.icon,
                currentTaskIndex = currentTaskIndex,
                totalTasks = tasks.size
            )
        }
    }
    // Cancel the notification when this screen is removed from composition
    DisposableEffect(Unit) {
        onDispose { RoutineProgressNotification.cancel(context) }
    }

    LaunchedEffect(isPlaying, timeRemainingSeconds) {
        if (isPlaying && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds -= 1
            totalElapsedSeconds += 1
        } else if (isPlaying && timeRemainingSeconds == 0 && currentTask != null) {
            if (currentTaskIndex < tasks.size - 1) {
                currentTaskIndex += 1
            } else {
                currentTaskIndex = tasks.size
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {
        if (currentTask == null) {
            if (tasks.isNotEmpty()) {
                CompletionScreen(
                    routine = routine,
                    tasks = tasks,
                    totalElapsedSeconds = totalElapsedSeconds,
                    onClose = onClose
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks available.", color = SubText)
                }
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Custom nav bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NeutralPill, CircleShape)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = NearBlack, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NearBlack,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            HorizontalDivider(thickness = 1.dp, color = BorderLight)

            Spacer(modifier = Modifier.height(14.dp))

            // Progress indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tasks.forEachIndexed { i, _ ->
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(6.dp)
                                .background(
                                    if (i == currentTaskIndex) BurntOrange else Color(0xFFC2E0DA),
                                    RoundedCornerShape(3.dp)
                                )
                        )
                    }
                }
                Text(
                    "Task ${currentTaskIndex + 1} of ${tasks.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubText
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Current task icon + name
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(BurntOrangeLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(currentTask.icon, fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = currentTask.name,
                style = MaterialTheme.typography.headlineMedium,
                color = NearBlack
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Circular progress timer — fills as you complete the task
            val totalSecs = currentTask.durationSeconds.toFloat().coerceAtLeast(1f)
            val elapsed = totalSecs - timeRemainingSeconds
            val rawProgress = (elapsed / totalSecs).coerceIn(0f, 1f)
            val fillProgress by animateFloatAsState(
                targetValue = rawProgress,
                animationSpec = tween(durationMillis = 800),
                label = "timer_arc"
            )

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 18.dp.toPx()

                    // Background track
                    drawArc(
                        color = Color(0xFFE5EDEB),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round)
                    )
                    // Fill arc — grows as time elapses
                    if (fillProgress > 0f) {
                        drawArc(
                            color = BurntOrange,
                            startAngle = -90f,
                            sweepAngle = 360f * fillProgress,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(timeRemainingSeconds),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2).sp,
                        color = NearBlack
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "REMAINING",
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time extender chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("+30s" to 30, "+1m" to 60, "+2m" to 120).forEach { (label, secs) ->
                    SuggestionChip(
                        onClick = { timeRemainingSeconds += secs },
                        label = {
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = NearBlack)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = NeutralPill),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Controls
            val pauseSource = remember { MutableInteractionSource() }
            val skipSource = remember { MutableInteractionSource() }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { isPlaying = !isPlaying },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurntOrange),
                    modifier = Modifier.height(56.dp).bouncyPress(pauseSource),
                    contentPadding = PaddingValues(horizontal = 28.dp),
                    interactionSource = pauseSource
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isPlaying) "Pause" else "Resume",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        if (currentTaskIndex < tasks.size - 1) {
                            currentTaskIndex += 1
                        } else {
                            currentTaskIndex = tasks.size
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurntOrangeLight),
                    modifier = Modifier.height(56.dp).bouncyPress(skipSource),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    interactionSource = skipSource
                ) {
                    Text("Skip", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = BurntOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Rounded.SkipNext, contentDescription = null, tint = BurntOrange, modifier = Modifier.size(18.dp))
                }
            }

            // Up next card
            val nextTask = tasks.getOrNull(currentTaskIndex + 1)
            if (nextTask != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "UP NEXT",
                    style = MaterialTheme.typography.labelLarge,
                    color = SubText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(BurntOrangeLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(nextTask.icon, fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(nextTask.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NearBlack)
                            Text(
                                formatTime(nextTask.durationSeconds).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = SubText
                            )
                        }
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = BorderLight, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletionScreen(
    routine: RoutineEntity,
    tasks: List<TaskEntity>,
    totalElapsedSeconds: Int,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Completion badge — filled ring shows 100% done
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 8.dp.toPx()
                drawArc(
                    color = BurntOrange,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = BurntOrange, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = routine.name,
            style = MaterialTheme.typography.labelLarge,
            color = SubText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Complete!",
            style = MaterialTheme.typography.headlineLarge,
            color = NearBlack,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats — hero layout, numbers are the focus
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BurntOrangeLight),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(totalElapsedSeconds),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = NearBlack,
                        letterSpacing = (-1.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("TIME SPENT", style = MaterialTheme.typography.labelSmall, color = Color(0xFF8A4A20))
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(52.dp)
                        .background(BurntOrange.copy(alpha = 0.2f))
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${tasks.size}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = NearBlack,
                        letterSpacing = (-1.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val word = if (tasks.size == 1) "TASK DONE" else "TASKS DONE"
                    Text(word, style = MaterialTheme.typography.labelSmall, color = Color(0xFF8A4A20))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val doneSource = remember { MutableInteractionSource() }
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .bouncyPress(doneSource),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BurntOrange),
            interactionSource = doneSource
        ) {
            Text("Back to Routines", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}
