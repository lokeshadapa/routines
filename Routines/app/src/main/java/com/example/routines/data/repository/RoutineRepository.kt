package com.example.routines.data.repository

import com.example.routines.data.local.RoutineDao
import com.example.routines.data.local.RoutineEntity
import com.example.routines.data.local.TaskDao
import com.example.routines.data.local.TaskEntity
import com.example.routines.data.local.TaskSummary
import kotlinx.coroutines.flow.Flow

class RoutineRepository(
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao
) {
    // Routine Operations
    fun getAllRoutines(): Flow<List<RoutineEntity>> = routineDao.getAllRoutines()

    suspend fun getRoutineById(id: Long): RoutineEntity? = routineDao.getRoutineById(id)

    suspend fun insertRoutine(routine: RoutineEntity): Long = routineDao.insertRoutine(routine)

    suspend fun deleteRoutine(routine: RoutineEntity) = routineDao.deleteRoutine(routine)

    suspend fun updateRoutine(routine: RoutineEntity) = routineDao.updateRoutine(routine)

    // Task Operations
    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>> = taskDao.getTasksForRoutine(routineId)

    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)

    suspend fun insertTasks(tasks: List<TaskEntity>) = taskDao.insertTasks(tasks)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTasksForRoutine(routineId: Long) = taskDao.deleteTasksForRoutine(routineId)

    fun getTaskSummary(routineId: Long): Flow<TaskSummary> = taskDao.getTaskSummary(routineId)
}
