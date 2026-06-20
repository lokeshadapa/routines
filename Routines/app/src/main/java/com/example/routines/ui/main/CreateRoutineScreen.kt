package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class DraftTask(
    val name: String = "",
    val durationSeconds: Int = 300,
    val icon: String = "💪"
)

val DURATION_PRESETS = listOf(
    "30s" to 30,
    "1m" to 60,
    "5m" to 300,
    "10m" to 600,
    "15m" to 900,
    "30m" to 1800
)

val ICON_CATEGORIES = listOf(
    "Fitness" to listOf("💪", "🏃", "🧘", "🚴", "🏊", "🤸", "🏋️", "⚽", "🥊", "🎽"),
    "Mind & Wellness" to listOf("🧠", "❤️", "🌿", "🌸", "🫁", "💤", "🕊️", "🙏", "✨", "🌙"),
    "Work & Focus" to listOf("💻", "📚", "✏️", "📝", "📊", "💡", "🎯", "⏰", "🗂️", "🔍"),
    "Food & Drink" to listOf("☕", "🍎", "🥗", "🍳", "💊", "💧", "🥤", "🍵", "🥑", "🧃"),
    "Daily Life" to listOf("🛁", "🛏️", "🚗", "🏠", "👔", "🌅", "🗓️", "📱", "🛒", "✈️"),
    "Creative" to listOf("🎨", "🎵", "🎸", "📷", "🖌️", "✍️", "🎬", "🎭", "🎤", "🎹")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    onBack: () -> Unit,
    onSave: (String, List<DraftTask>) -> Unit,
    initialName: String = "",
    initialTasks: List<DraftTask> = listOf(DraftTask("Warmup", 300, "🏃")),
    isEditMode: Boolean = false,
    onDelete: (() -> Unit)? = null
) {
    var routineName by remember { mutableStateOf(initialName) }
    // Re-init when initialTasks first populates (edit mode: tasks load after first composition)
    var draftTasks by remember(initialTasks.size) { mutableStateOf(initialTasks) }

    var draggingIndex by remember { mutableIntStateOf(-1) }
    var pendingDragPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Routine?") },
            text = { Text("This will permanently delete this routine and all its tasks.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete?.invoke()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "EDIT ROUTINE" else "CREATE ROUTINE",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditMode && onDelete != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Delete Routine",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            if (routineName.isNotBlank() && draftTasks.isNotEmpty()) {
                                onSave(routineName, draftTasks)
                            }
                        },
                        enabled = routineName.isNotBlank() && draftTasks.isNotEmpty()
                    ) {
                        Text("SAVE", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Routine Name") },
                placeholder = { Text("e.g., Morning Workout") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TASKS",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            draftTasks.forEachIndexed { index, task ->
                DraftTaskCard(
                    task = task,
                    isDragging = draggingIndex == index,
                    onTaskChange = { updated ->
                        val newList = draftTasks.toMutableList()
                        newList[index] = updated
                        draftTasks = newList
                    },
                    onDragStart = {
                        draggingIndex = index
                        pendingDragPx = 0f
                    },
                    onDrag = { deltaY ->
                        pendingDragPx += deltaY
                        val threshold = with(density) { 100.dp.toPx() }
                        if (pendingDragPx > threshold && draggingIndex < draftTasks.lastIndex) {
                            draftTasks = draftTasks.toMutableList().apply {
                                add(draggingIndex + 1, removeAt(draggingIndex))
                            }
                            draggingIndex++
                            pendingDragPx -= threshold
                        } else if (pendingDragPx < -threshold && draggingIndex > 0) {
                            draftTasks = draftTasks.toMutableList().apply {
                                add(draggingIndex - 1, removeAt(draggingIndex))
                            }
                            draggingIndex--
                            pendingDragPx += threshold
                        }
                    },
                    onDragEnd = {
                        draggingIndex = -1
                        pendingDragPx = 0f
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedButton(
                onClick = { draftTasks = draftTasks + DraftTask("New Task", 300, "✨") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("ADD NEW TASK", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftTaskCard(
    task: DraftTask,
    isDragging: Boolean,
    onTaskChange: (DraftTask) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    var showIconPicker by remember { mutableStateOf(false) }

    val isPreset = DURATION_PRESETS.any { it.second == task.durationSeconds }
    var showCustom by remember { mutableStateOf(!isPreset) }
    var customMinsText by remember { mutableStateOf("${task.durationSeconds / 60}") }
    var customSecsText by remember { mutableStateOf("${task.durationSeconds % 60}") }

    val mins = task.durationSeconds / 60
    val secs = task.durationSeconds % 60
    val durationLabel = when {
        mins == 0 -> "${secs}s"
        secs == 0 -> "${mins}m"
        else -> "${mins}m ${secs}s"
    }

    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            IconPickerSheetContent(
                selectedIcon = task.icon,
                onIconSelected = { icon ->
                    onTaskChange(task.copy(icon = icon))
                    showIconPicker = false
                }
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 0.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.DragHandle,
                    contentDescription = "Drag to reorder",
                    tint = if (isDragging)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { onDragStart() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(dragAmount.y)
                            },
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragEnd
                        )
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))

                // Tappable icon circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { showIconPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.icon, style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = task.name,
                    onValueChange = { onTaskChange(task.copy(name = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Task name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Duration: $durationLabel",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Duration preset chips + Custom chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DURATION_PRESETS.forEach { (label, secs) ->
                    FilterChip(
                        selected = !showCustom && task.durationSeconds == secs,
                        onClick = {
                            showCustom = false
                            onTaskChange(task.copy(durationSeconds = secs))
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
                FilterChip(
                    selected = showCustom,
                    onClick = {
                        showCustom = true
                        customMinsText = "${task.durationSeconds / 60}"
                        customSecsText = "${task.durationSeconds % 60}"
                    },
                    label = { Text("Custom") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Custom duration inputs
            if (showCustom) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customMinsText,
                        onValueChange = { v ->
                            val filtered = v.filter { it.isDigit() }.take(2)
                            customMinsText = filtered
                            val total = (filtered.toIntOrNull() ?: 0) * 60 + (customSecsText.toIntOrNull() ?: 0)
                            if (total > 0) onTaskChange(task.copy(durationSeconds = total))
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Min") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Text(
                        ":",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = customSecsText,
                        onValueChange = { v ->
                            val filtered = v.filter { it.isDigit() }.take(2)
                            val capped = filtered.toIntOrNull()?.coerceAtMost(59)?.toString() ?: filtered
                            customSecsText = capped
                            val total = (customMinsText.toIntOrNull() ?: 0) * 60 + (capped.toIntOrNull() ?: 0)
                            if (total > 0) onTaskChange(task.copy(durationSeconds = total))
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Sec") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
fun IconPickerSheetContent(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Choose Icon",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ICON_CATEGORIES.forEach { (categoryName, icons) ->
            Text(
                categoryName.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            icons.chunked(5).forEach { rowIcons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowIcons.forEach { icon ->
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    if (icon == selectedIcon)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable { onIconSelected(icon) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(icon, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    // Pad incomplete rows
                    repeat(5 - rowIcons.size) {
                        Spacer(modifier = Modifier.size(52.dp))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
