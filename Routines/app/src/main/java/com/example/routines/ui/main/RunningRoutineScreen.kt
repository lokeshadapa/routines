package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskEntity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunningRoutineScreen(
    routine: RoutineEntity,
    tasks: List<TaskEntity>,
    onClose: () -> Unit
) {
    // For a real app, this state would likely live in a dedicated ViewModel to survive configuration changes.
    var currentTaskIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    
    val currentTask = tasks.getOrNull(currentTaskIndex)
    
    // Initializing the timer based on the current task
    var timeRemainingSeconds by remember(currentTask) { 
        mutableIntStateOf(currentTask?.durationSeconds ?: 0) 
    }

    // The Timer loop
    LaunchedEffect(isPlaying, timeRemainingSeconds) {
        if (isPlaying && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds -= 1
        } else if (isPlaying && timeRemainingSeconds == 0 && currentTask != null) {
            // Auto advance when timer finishes; set to tasks.size to trigger completion state
            if (currentTaskIndex < tasks.size - 1) {
                currentTaskIndex += 1
            } else {
                currentTaskIndex = tasks.size
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routine.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close Routine")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (currentTask == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (tasks.isEmpty()) {
                    Text(
                        "No tasks available.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text("🎉", style = MaterialTheme.typography.displayLarge)
                        Text(
                            "Routine Complete!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Great work on completing ${routine.name}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onClose,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Current Task Display
            Text(
                text = "${currentTask.icon} ${currentTask.name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Large Timer Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                CircularProgressIndicator(
                    progress = { 
                        if (currentTask.durationSeconds == 0) 1f 
                        else timeRemainingSeconds.toFloat() / currentTask.durationSeconds.toFloat() 
                    },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Text(
                    text = formatTime(timeRemainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extend Time Button
            FilledTonalButton(
                onClick = { timeRemainingSeconds += 60 },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("+ 1 MIN", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { isPlaying = !isPlaying },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                FilledIconButton(
                    onClick = {
                        if (currentTaskIndex < tasks.size - 1) {
                            currentTaskIndex += 1
                        } else {
                            currentTaskIndex = tasks.size
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "Skip to Next")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Up Next Section
            val upcomingTasks = tasks.drop(currentTaskIndex + 1)
            if (upcomingTasks.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "UP NEXT",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(upcomingTasks) { upcoming ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${upcoming.icon} ${upcoming.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formatTime(upcoming.durationSeconds),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
