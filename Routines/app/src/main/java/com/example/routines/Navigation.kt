package com.example.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.routines.theme.BurntOrange
import com.example.routines.theme.CardWhite
import com.example.routines.theme.NunitoFontFamily
import com.example.routines.theme.SubText
import com.example.routines.ui.main.CreateRoutineScreen
import com.example.routines.ui.main.DraftTask
import com.example.routines.ui.main.HomeScreen
import com.example.routines.ui.main.RunningRoutineScreen
import com.example.routines.ui.main.TemplatesScreen
import com.example.routines.ui.viewmodel.RoutineViewModel

@Composable
private fun NavPill(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) BurntOrange else Color.Transparent,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f),
        label = "${label}_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) Color.White else SubText,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f),
        label = "${label}_fg"
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Crossfade(targetState = selected, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f), label = "${label}_icon") { sel ->
            Icon(
                imageVector = if (sel) selectedIcon else icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(27.dp)
            )
        }
        AnimatedVisibility(
            visible = selected,
            enter = expandHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 380f)) + fadeIn(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f)),
            exit = shrinkHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 380f)) + fadeOut(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f))
        ) {
            Text(
                text = label,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

sealed class Screen {
    object Home : Screen()
    object Templates : Screen()
    object CreateRoutine : Screen()
    data class EditRoutine(val routineId: Long) : Screen()
    data class RunningRoutine(val routineId: Long) : Screen()
    data class CreateRoutineFromTemplate(val name: String, val tasks: List<DraftTask>) : Screen()
}

@Composable
fun MainNavigation(viewModel: RoutineViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val showBottomNav = currentScreen is Screen.Home || currentScreen is Screen.Templates

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f)) togetherWith fadeOut(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f))
            },
            label = "screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { screen ->
            when (screen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onCreateRoutineClick = { currentScreen = Screen.CreateRoutine },
                        onEditRoutineClick = { routineId -> currentScreen = Screen.EditRoutine(routineId) },
                        onPlayRoutineClick = { routineId -> currentScreen = Screen.RunningRoutine(routineId) },
                        onExploreClick = { currentScreen = Screen.Templates },
                        onUseTemplate = { name, tasks -> currentScreen = Screen.CreateRoutineFromTemplate(name, tasks) }
                    )
                }
                is Screen.Templates -> {
                    TemplatesScreen(
                        onUseTemplate = { name, tasks -> currentScreen = Screen.CreateRoutineFromTemplate(name, tasks) }
                    )
                }
                is Screen.CreateRoutine -> {
                    CreateRoutineScreen(
                        onBack = { currentScreen = Screen.Home },
                        onSave = { name, icon, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                            viewModel.saveRoutineWithTasks(name, icon, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
                            currentScreen = Screen.Home
                        }
                    )
                }
                is Screen.CreateRoutineFromTemplate -> {
                    CreateRoutineScreen(
                        initialName = screen.name,
                        initialTasks = screen.tasks,
                        onBack = { currentScreen = Screen.Templates },
                        onSave = { name, icon, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                            viewModel.saveRoutineWithTasks(name, icon, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
                            currentScreen = Screen.Home
                        }
                    )
                }
                is Screen.EditRoutine -> {
                    val routines by viewModel.allRoutines.collectAsState()
                    val routine = routines.find { it.id == screen.routineId }
                    val tasks by viewModel.getTasksForRoutine(screen.routineId)
                        .collectAsState(initial = emptyList())
                    if (routine != null) {
                        CreateRoutineScreen(
                            initialName = routine.name,
                            initialRoutineIcon = routine.icon,
                            initialTasks = tasks.map { DraftTask(it.name, it.durationSeconds, it.icon) },
                            initialDaysOfWeek = routine.daysOfWeek,
                            initialReminderEnabled = routine.reminderEnabled,
                            initialReminderHour = routine.reminderHour,
                            initialReminderMinute = routine.reminderMinute,
                            isEditMode = true,
                            onBack = { currentScreen = Screen.Home },
                            onSave = { name, icon, draftTasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                                viewModel.updateRoutineWithTasks(screen.routineId, name, icon, draftTasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
                                currentScreen = Screen.Home
                            },
                            onDelete = {
                                viewModel.deleteRoutine(routine)
                                currentScreen = Screen.Home
                            }
                        )
                    } else {
                        currentScreen = Screen.Home
                    }
                }
                is Screen.RunningRoutine -> {
                    val routines by viewModel.allRoutines.collectAsState()
                    val routine = routines.find { it.id == screen.routineId }
                    val tasks by viewModel.getTasksForRoutine(screen.routineId)
                        .collectAsState(initial = emptyList())
                    if (routine != null) {
                        RunningRoutineScreen(
                            routine = routine,
                            tasks = tasks,
                            onClose = { currentScreen = Screen.Home }
                        )
                    } else {
                        currentScreen = Screen.Home
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showBottomNav,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 200f)) { it } + fadeIn(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f)),
            exit = slideOutVertically(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 200f)) { it } + fadeOut(spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 1600f))
        ) {
            val navShape = RoundedCornerShape(40.dp)
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .shadow(elevation = 16.dp, shape = navShape, ambientColor = Color(0x22000000), spotColor = Color(0x33000000))
                        .clip(navShape)
                        .background(CardWhite)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val homeSelected = currentScreen is Screen.Home
                    val exploreSelected = currentScreen is Screen.Templates
                    NavPill(
                        selected = homeSelected,
                        label = "Home",
                        icon = Icons.Outlined.Home,
                        selectedIcon = Icons.Rounded.Home,
                        onClick = { currentScreen = Screen.Home }
                    )
                    NavPill(
                        selected = exploreSelected,
                        label = "Explore",
                        icon = Icons.Outlined.Explore,
                        selectedIcon = Icons.Rounded.Explore,
                        onClick = { currentScreen = Screen.Templates }
                    )
                }
            }
        }
    }
}
