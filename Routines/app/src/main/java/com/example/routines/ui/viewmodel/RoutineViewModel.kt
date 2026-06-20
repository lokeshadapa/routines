package com.example.routines.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskEntity
import com.example.routines.data.repository.RoutineRepository
import com.example.routines.ui.main.DraftTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val repository: RoutineRepository
) : ViewModel() {

    val allRoutines: StateFlow<List<RoutineEntity>> = repository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>> {
        return repository.getTasksForRoutine(routineId)
    }

    fun addRoutine(name: String) {
        viewModelScope.launch {
            repository.insertRoutine(RoutineEntity(name = name))
        }
    }

    fun saveRoutineWithTasks(name: String, tasks: List<DraftTask>) {
        viewModelScope.launch {
            val routineId = repository.insertRoutine(RoutineEntity(name = name))
            val taskEntities = tasks.mapIndexed { index, draft ->
                TaskEntity(
                    routineId = routineId,
                    name = draft.name,
                    durationSeconds = draft.durationSeconds,
                    icon = draft.icon,
                    orderPosition = index
                )
            }
            repository.insertTasks(taskEntities)
        }
    }

    fun updateRoutineWithTasks(routineId: Long, name: String, tasks: List<DraftTask>) {
        viewModelScope.launch {
            val existing = repository.getRoutineById(routineId) ?: return@launch
            repository.updateRoutine(existing.copy(name = name, lastModifiedAt = System.currentTimeMillis()))
            repository.deleteTasksForRoutine(routineId)
            repository.insertTasks(tasks.mapIndexed { index, draft ->
                TaskEntity(
                    routineId = routineId,
                    name = draft.name,
                    durationSeconds = draft.durationSeconds,
                    icon = draft.icon,
                    orderPosition = index
                )
            })
        }
    }

    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            repository.deleteRoutine(routine)
        }
    }

    // Factory for manual DI (no Hilt)
    class Factory(private val repository: RoutineRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RoutineViewModel(repository) as T
        }
    }
}
