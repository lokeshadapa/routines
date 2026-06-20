package com.example.routines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.routines.data.local.AppDatabase
import com.example.routines.data.repository.RoutineRepository
import com.example.routines.theme.RoutinesTheme
import com.example.routines.ui.viewmodel.RoutineViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RoutineViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        RoutineViewModel.Factory(RoutineRepository(db.routineDao(), db.taskDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoutinesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainNavigation(viewModel)
                }
            }
        }
    }
}
