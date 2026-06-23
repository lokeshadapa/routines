package com.example.routines.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routines.theme.*

data class DraftTask(
    val name: String = "",
    val durationSeconds: Int = 300,
    val icon: String = "💪"
)

val ICON_CATEGORIES = listOf(
    "Fitness"        to listOf("💪", "🏃", "🧘", "🚴", "🏊", "🤸", "🏋️", "⚽", "🥊", "🎽"),
    "Mind"           to listOf("🧠", "❤️", "🌿", "🌸", "🫁", "💤", "🕊️", "🙏", "✨", "🌙"),
    "Work"           to listOf("💻", "📚", "✏️", "📝", "📊", "💡", "🎯", "⏰", "🗂️", "🔍"),
    "Food"           to listOf("☕", "🍎", "🥗", "🍳", "💊", "💧", "🥤", "🍵", "🥑", "🧃"),
    "Daily"          to listOf("🛁", "🛏️", "🚗", "🏠", "👔", "🌅", "🗓️", "📱", "🛒", "✈️"),
    "Creative"       to listOf("🎨", "🎵", "🎸", "📷", "🖌️", "✍️", "🎬", "🎭", "🎤", "🎹")
)

private fun formatDurationHms(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%02d:%02d:%02d", h, m, s)
    else String.format("%02d:%02d", m, s)
}

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
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(WarmWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = 88.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Custom nav bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NeutralPill, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = NearBlack, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = if (isEditMode) "Edit Routine" else "New Routine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NearBlack,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
                if (isEditMode && onDelete != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFEE4E4), CircleShape)
                            .clickable { showDeleteDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFE53E3E), modifier = Modifier.size(16.dp))
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = BorderLight)

            // Routine name input card
            Card(
                modifier = Modifier
                    .padding(top = 18.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        text = "ROUTINE NAME",
                        style = MaterialTheme.typography.labelSmall,
                        color = SubText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicTextField(
                        value = routineName,
                        onValueChange = { routineName = it },
                        textStyle = TextStyle(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = NearBlack,
                            letterSpacing = (-0.3).sp
                        ),
                        singleLine = true,
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(BurntOrange),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (routineName.isEmpty()) {
                                Text("e.g. Morning Workout", style = TextStyle(
                                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp, color = SubText, letterSpacing = (-0.3).sp
                                ))
                            }
                            inner()
                        }
                    )
                }
            }

            // Tasks section header — no divider (design spec)
            Row(
                modifier = Modifier
                    .padding(top = 22.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASKS",
                    style = MaterialTheme.typography.labelLarge,
                    color = NearBlack
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(BurntOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${draftTasks.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                            lineHeight = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            draftTasks.forEachIndexed { index, task ->
                DraftTaskCard(
                    task = task,
                    isDragging = draggingIndex == index,
                    onTaskChange = { updated ->
                        val newList = draftTasks.toMutableList()
                        newList[index] = updated
                        draftTasks = newList
                    },
                    onDelete = {
                        draftTasks = draftTasks.toMutableList().also { it.removeAt(index) }
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
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }

            // Add Task button — dashed warm-orange border per design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardWhite)
                    .drawBehind {
                        drawRoundRect(
                            color = Color(0xFFE8D0C0),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                            )
                        )
                    }
                    .clickable { draftTasks = draftTasks + DraftTask("New Task", 300, "✨") }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(22.dp).background(BurntOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                    Text("Add Task", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BurntOrange)
                }
            }
        }

        // Gradient + save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, WarmWhite)
                    )
                )
        )
        Button(
            onClick = {
                if (routineName.isNotBlank() && draftTasks.isNotEmpty()) {
                    onSave(routineName, draftTasks)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BurntOrange)
        ) {
            Text("Save Routine", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftTaskCard(
    task: DraftTask,
    isDragging: Boolean,
    onTaskChange: (DraftTask) -> Unit,
    onDelete: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showIconPicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }

    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = CardWhite,
            dragHandle = null
        ) {
            IconPickerSheetContent(
                taskName = task.name,
                selectedIcon = task.icon,
                onIconSelected = { icon ->
                    onTaskChange(task.copy(icon = icon))
                    showIconPicker = false
                }
            )
        }
    }

    if (showDurationPicker) {
        ModalBottomSheet(
            onDismissRequest = { showDurationPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = CardWhite,
            dragHandle = null
        ) {
            DurationPickerSheetContent(
                taskName = task.name,
                initialSeconds = task.durationSeconds,
                onConfirm = { seconds ->
                    onTaskChange(task.copy(durationSeconds = seconds))
                    showDurationPicker = false
                }
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = "Drag to reorder",
                tint = Color(0xFFCACACA),
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

            Box(modifier = Modifier.size(42.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(BurntOrangeLight, CircleShape)
                        .clickable { showIconPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.icon, fontSize = 20.sp)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(BurntOrange, CircleShape)
                        .border(1.5.dp, CardWhite, CircleShape)
                        .clickable { showIconPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(7.dp))
                }
            }

            BasicTextField(
                value = task.name,
                onValueChange = { onTaskChange(task.copy(name = it)) },
                textStyle = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NearBlack
                ),
                singleLine = true,
                cursorBrush = androidx.compose.ui.graphics.SolidColor(BurntOrange),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (task.name.isEmpty()) {
                        Text("Task name", style = TextStyle(
                            fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = SubText
                        ))
                    }
                    inner()
                }
            )

            // Duration pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NeutralPill,
                modifier = Modifier.clickable { showDurationPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        formatDurationHms(task.durationSeconds),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NearBlack
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = SubText, modifier = Modifier.size(10.dp))
                }
            }

            // Delete button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFFEE4E4), CircleShape)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Delete", tint = Color(0xFFE53E3E), modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun DurationPickerSheetContent(
    taskName: String,
    initialSeconds: Int,
    onConfirm: (Int) -> Unit
) {
    var buffer by remember {
        mutableStateOf(
            String.format(
                "%02d%02d%02d",
                initialSeconds / 3600,
                (initialSeconds % 3600) / 60,
                initialSeconds % 60
            )
        )
    }

    val hh = buffer.substring(0, 2).toIntOrNull() ?: 0
    val mm = buffer.substring(2, 4).toIntOrNull() ?: 0
    val ss = buffer.substring(4, 6).toIntOrNull() ?: 0
    val totalSeconds = hh * 3600 + mm * 60 + ss

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        // Header — no task name pill
        Text("Set Duration", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)

        // HH:MM:SS display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            TimeUnitDisplay(value = hh, label = "hr")
            Text(":", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = SubText,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp))
            TimeUnitDisplay(value = mm, label = "min")
            Text(":", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = SubText,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp))
            TimeUnitDisplay(value = ss, label = "sec")
        }

        // Numpad — plain rows, no fixed height constraint
        val numpadRows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "back")
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            numpadRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        when (key) {
                            "" -> Spacer(modifier = Modifier.weight(1f).height(58.dp))
                            "back" -> Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BurntOrangeLight,
                                modifier = Modifier.weight(1f).height(58.dp).clickable {
                                    buffer = "0" + buffer.dropLast(1)
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(Icons.AutoMirrored.Rounded.Backspace, contentDescription = "Backspace", tint = BurntOrange)
                                }
                            }
                            else -> Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = WarmWhite,
                                modifier = Modifier.weight(1f).height(58.dp).clickable {
                                    buffer = (buffer + key).takeLast(6)
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(key, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NearBlack, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onConfirm(totalSeconds) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BurntOrange)
        ) {
            Text("Confirm", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

@Composable
private fun TimeUnitDisplay(value: Int, label: String) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(String.format("%02d", value), fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = NearBlack)
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SubText,
            modifier = Modifier.padding(bottom = 6.dp))
    }
}

@Composable
fun IconPickerSheetContent(
    taskName: String,
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ICON_CATEGORIES[0].first) }
    var searchQuery by remember { mutableStateOf("") }

    val icons = if (searchQuery.isNotBlank()) {
        ICON_CATEGORIES
            .filter { (name, _) -> name.contains(searchQuery, ignoreCase = true) }
            .flatMap { it.second }
            .ifEmpty { ICON_CATEGORIES.flatMap { it.second } }
    } else {
        ICON_CATEGORIES.first { it.first == selectedCategory }.second
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        // Header — no task name pill
        Text("Choose Icon", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = NearBlack)
        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = WarmWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = SubText, modifier = Modifier.size(18.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = NearBlack
                    ),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(BurntOrange),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            androidx.compose.material3.Text(
                                "Search by category…",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = SubText
                                )
                            )
                        }
                        inner()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = SubText,
                        modifier = Modifier.size(16.dp).clickable { searchQuery = "" }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Category tabs — hidden while searching
        if (searchQuery.isEmpty()) {
            LazyRow(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ICON_CATEGORIES) { (name, _) ->
                    val isSelected = name == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BurntOrange else WarmWhite,
                        modifier = Modifier.clickable { selectedCategory = name }
                    ) {
                        Text(
                            name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else SubText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Icon grid — weight(1f) fills all remaining space
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp)
        ) {
            items(icons) { icon ->
                val isSelected = icon == selectedIcon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(if (isSelected) BurntOrange else NeutralPill, CircleShape)
                        .clickable { onIconSelected(icon) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 26.sp)
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(18.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
                        }
                    }
                }
            }
        }
    }
}
