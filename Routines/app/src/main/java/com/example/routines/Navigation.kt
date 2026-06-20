package com.example.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import com.example.routines.ui.main.CreateRoutineScreen
import com.example.routines.ui.main.DraftTask
import com.example.routines.ui.main.HomeScreen
import com.example.routines.ui.main.RunningRoutineScreen
import com.example.routines.ui.viewmodel.RoutineViewModel

sealed class Screen {
    object Home : Screen()
    object CreateRoutine : Screen()
    data class EditRoutine(val routineId: Long) : Screen()
    data class RunningRoutine(val routineId: Long) : Screen()
}

@Composable
fun MainNavigation(viewModel: RoutineViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            val direction = if (targetState is Screen.Home) {
                AnimatedContentTransitionScope.SlideDirection.End
            } else {
                AnimatedContentTransitionScope.SlideDirection.Start
            }
            slideIntoContainer(direction, tween(350)) + fadeIn(tween(200)) togetherWith
                slideOutOfContainer(direction, tween(350)) + fadeOut(tween(200))
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onCreateRoutineClick = { currentScreen = Screen.CreateRoutine },
                    onEditRoutineClick = { routineId -> currentScreen = Screen.EditRoutine(routineId) },
                    onPlayRoutineClick = { routineId -> currentScreen = Screen.RunningRoutine(routineId) }
                )
            }
            is Screen.CreateRoutine -> {
                CreateRoutineScreen(
                    onBack = { currentScreen = Screen.Home },
                    onSave = { name, tasks ->
                        viewModel.saveRoutineWithTasks(name, tasks)
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
                        isEditMode = true,
                        onBack = { currentScreen = Screen.Home },
                        onSave = { name, draftTasks ->
                            viewModel.updateRoutineWithTasks(screen.routineId, name, draftTasks)
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
}
