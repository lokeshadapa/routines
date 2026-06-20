package com.example.routines.di

import android.content.Context
import com.example.routines.data.local.AppDatabase
import com.example.routines.data.local.RoutineDao
import com.example.routines.data.local.TaskDao
import com.example.routines.data.repository.RoutineRepository

object DatabaseModule {

    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDao {
        return appDatabase.routineDao()
    }

    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    fun provideRoutineRepository(
        routineDao: RoutineDao,
        taskDao: TaskDao
    ): RoutineRepository {
        return RoutineRepository(routineDao, taskDao)
    }
}
