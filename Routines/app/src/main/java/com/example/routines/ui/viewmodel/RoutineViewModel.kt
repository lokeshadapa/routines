package com.example.routines.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.routines.ReminderScheduler
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskEntity
import com.example.routines.data.local.TaskSummary
import com.example.routines.data.repository.RoutineRepository
import com.example.routines.ui.main.DraftTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoutineViewModel(
    application: Application,
    private val repository: RoutineRepository
) : AndroidViewModel(application) {

    val allRoutines: StateFlow<List<RoutineEntity>> = repository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>> {
        return repository.getTasksForRoutine(routineId)
    }

    fun getTaskSummary(routineId: Long): Flow<TaskSummary> = repository.getTaskSummary(routineId)

    fun addRoutine(name: String) {
        viewModelScope.launch {
            repository.insertRoutine(RoutineEntity(name = name))
        }
    }

    fun saveRoutineWithTasks(
        name: String, icon: String = "", tasks: List<DraftTask>, daysOfWeek: Int = 0,
        reminderEnabled: Boolean = false, reminderHour: Int = 8, reminderMinute: Int = 0
    ) {
        viewModelScope.launch {
            val entity = RoutineEntity(
                name = name, icon = icon, daysOfWeek = daysOfWeek,
                reminderEnabled = reminderEnabled, reminderHour = reminderHour, reminderMinute = reminderMinute
            )
            val routineId = repository.insertRoutine(entity)
            repository.insertTasks(tasks.mapIndexed { index, draft ->
                TaskEntity(routineId = routineId, name = draft.name, durationSeconds = draft.durationSeconds, icon = draft.icon, orderPosition = index)
            })
            ReminderScheduler.scheduleReminder(getApplication(), entity.copy(id = routineId))
        }
    }

    fun updateRoutineWithTasks(
        routineId: Long, name: String, icon: String = "", tasks: List<DraftTask>, daysOfWeek: Int = 0,
        reminderEnabled: Boolean = false, reminderHour: Int = 8, reminderMinute: Int = 0
    ) {
        viewModelScope.launch {
            val existing = repository.getRoutineById(routineId) ?: return@launch
            val updated = existing.copy(
                name = name, icon = icon, daysOfWeek = daysOfWeek,
                reminderEnabled = reminderEnabled, reminderHour = reminderHour, reminderMinute = reminderMinute,
                lastModifiedAt = System.currentTimeMillis()
            )
            repository.updateRoutine(updated)
            repository.deleteTasksForRoutine(routineId)
            repository.insertTasks(tasks.mapIndexed { index, draft ->
                TaskEntity(routineId = routineId, name = draft.name, durationSeconds = draft.durationSeconds, icon = draft.icon, orderPosition = index)
            })
            ReminderScheduler.scheduleReminder(getApplication(), updated)
        }
    }

    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), routine.id)
            repository.deleteRoutine(routine)
        }
    }

    // Factory for manual DI (no Hilt)
    class Factory(private val application: Application, private val repository: RoutineRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return RoutineViewModel(application, repository) as T
        }
    }
}
