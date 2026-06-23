package com.example.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        onSave = { name, tasks ->
                            viewModel.saveRoutineWithTasks(name, tasks)
                            currentScreen = Screen.Home
                        }
                    )
                }
                is Screen.CreateRoutineFromTemplate -> {
                    CreateRoutineScreen(
                        initialName = screen.name,
                        initialTasks = screen.tasks,
                        onBack = { currentScreen = Screen.Templates },
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

        AnimatedVisibility(
            visible = showBottomNav,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it } + fadeIn(tween(200)),
            exit = slideOutVertically { it } + fadeOut(tween(200))
        ) {
            NavigationBar(
                containerColor = CardWhite,
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = currentScreen is Screen.Home,
                    onClick = { currentScreen = Screen.Home },
                    icon = {
                        Icon(Icons.Rounded.Home, contentDescription = "Home")
                    },
                    label = {
                        Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BurntOrange,
                        selectedTextColor = BurntOrange,
                        indicatorColor = BurntOrangeLight,
                        unselectedIconColor = SubText,
                        unselectedTextColor = SubText
                    )
                )
                NavigationBarItem(
                    selected = currentScreen is Screen.Templates,
                    onClick = { currentScreen = Screen.Templates },
                    icon = {
                        Icon(Icons.Rounded.Explore, contentDescription = "Explore")
                    },
                    label = {
                        Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
