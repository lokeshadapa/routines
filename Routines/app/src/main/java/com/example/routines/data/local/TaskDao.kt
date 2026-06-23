package com.example.routines.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

data class TaskSummary(val taskCount: Int, val totalDuration: Int)

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY orderPosition ASC")
    fun getTasksForRoutine(routineId: Long): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) as taskCount, COALESCE(SUM(durationSeconds), 0) as totalDuration FROM tasks WHERE routineId = :routineId")
    fun getTaskSummary(routineId: Long): Flow<TaskSummary>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE routineId = :routineId")
    suspend fun deleteTasksForRoutine(routineId: Long)
}
