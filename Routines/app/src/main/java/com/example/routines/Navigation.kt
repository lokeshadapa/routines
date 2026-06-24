package com.example.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.routines.theme.BurntOrange
import com.example.routines.theme.BurntOrangeLight
import com.example.routines.theme.CardWhite
import com.example.routines.theme.SubText
import com.example.routines.ui.main.CreateRoutineScreen
import com.example.routines.ui.main.DraftTask
import com.example.routines.ui.main.HomeScreen
import com.example.routines.ui.main.RunningRoutineScreen
import com.example.routines.ui.main.TemplatesScreen
import com.example.routines.ui.viewmodel.RoutineViewModel

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
                val isTabSwitch =
                    (initialState is Screen.Home && targetState is Screen.Templates) ||
                    (initialState is Screen.Templates && targetState is Screen.Home)
                when {
                    isTabSwitch ->
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    targetState is Screen.Home || targetState is Screen.Templates ->
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(350)) +
                            fadeIn(tween(200)) togetherWith
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(350)) +
                            fadeOut(tween(200))
                    else ->
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(350)) +
                            fadeIn(tween(200)) togetherWith
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(350)) +
                            fadeOut(tween(200))
                }
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
                        onSave = { name, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                            viewModel.saveRoutineWithTasks(name, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
                            currentScreen = Screen.Home
                        }
                    )
                }
                is Screen.CreateRoutineFromTemplate -> {
                    CreateRoutineScreen(
                        initialName = screen.name,
                        initialTasks = screen.tasks,
                        onBack = { currentScreen = Screen.Templates },
                        onSave = { name, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                            viewModel.saveRoutineWithTasks(name, tasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
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
                            initialTasks = tasks.map { DraftTask(it.name, it.durationSeconds, it.icon) },
                            initialDaysOfWeek = routine.daysOfWeek,
                            initialReminderEnabled = routine.reminderEnabled,
                            initialReminderHour = routine.reminderHour,
                            initialReminderMinute = routine.reminderMinute,
                            isEditMode = true,
                            onBack = { currentScreen = Screen.Home },
                            onSave = { name, draftTasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute ->
                                viewModel.updateRoutineWithTasks(screen.routineId, name, draftTasks, daysOfWeek, reminderEnabled, reminderHour, reminderMinute)
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
            enter = slideInVertically { it } + fadeIn(tween(200)),
            exit = slideOutVertically { it } + fadeOut(tween(200))
        ) {
            val navShape = RoundedCornerShape(28.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                NavigationBar(
                    containerColor = CardWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 16.dp, shape = navShape, ambientColor = Color(0x22000000), spotColor = Color(0x33000000))
                        .clip(navShape)
                ) {
                    val homeSelected = currentScreen is Screen.Home
                    NavigationBarItem(
                        selected = homeSelected,
                        onClick = { currentScreen = Screen.Home },
                        icon = {
                            Crossfade(targetState = homeSelected, animationSpec = tween(220), label = "home_icon") { sel ->
                                Icon(if (sel) Icons.Rounded.Home else Icons.Outlined.Home, contentDescription = "Home")
                            }
                        },
                        label = {
                            Text(
                                "Home",
                                fontSize = 11.sp,
                                fontWeight = if (homeSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BurntOrange,
                            selectedTextColor = BurntOrange,
                            indicatorColor = BurntOrangeLight,
                            unselectedIconColor = SubText,
                            unselectedTextColor = SubText
                        )
                    )
                    val exploreSelected = currentScreen is Screen.Templates
                    NavigationBarItem(
                        selected = exploreSelected,
                        onClick = { currentScreen = Screen.Templates },
                        icon = {
                            Crossfade(targetState = exploreSelected, animationSpec = tween(220), label = "explore_icon") { sel ->
                                Icon(if (sel) Icons.Rounded.Explore else Icons.Outlined.Explore, contentDescription = "Explore")
                            }
                        },
                        label = {
                            Text(
                                "Explore",
                                fontSize = 11.sp,
                                fontWeight = if (exploreSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BurntOrange,
                            selectedTextColor = BurntOrange,
                            indicatorColor = BurntOrangeLight,
                            unselectedIconColor = SubText,
                            unselectedTextColor = SubText
                        )
                    )
                }
            }
        }
    }
}
