package com.example.routines.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.routines.data.local.RoutineEntity
import com.example.routines.ui.viewmodel.RoutineViewModel
import kotlinx.coroutines.delay

val QUOTES = listOf(
    "The secret of your future is hidden in your daily routine.",
    "Motivation is what gets you started. Habit is what keeps you going.",
    "Quality is not an act, it is a habit.",
    "We are what we repeatedly do.",
    "Small daily improvements are the key to staggering long-term results."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RoutineViewModel,
    onCreateRoutineClick: () -> Unit,
    onEditRoutineClick: (Long) -> Unit,
    onPlayRoutineClick: (Long) -> Unit
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ROUTINES",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateRoutineClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Routine")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"$displayedQuote\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your Routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (routines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No routines yet. Create one!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(routines, key = { it.id }) { routine ->
                        RoutineCard(
                            routine = routine,
                            onEditClick = { onEditRoutineClick(routine.id) },
                            onPlayClick = { onPlayRoutineClick(routine.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineCard(
    routine: RoutineEntity,
    onEditClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .clickable(onClick = onEditClick)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            FilledIconButton(
                onClick = onPlayClick,
                modifier = Modifier.padding(end = 12.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Start Routine")
            }
        }
    }
}
