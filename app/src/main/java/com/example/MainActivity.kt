package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.database.TrackerDatabase
import com.example.data.repository.TrackerRepository
import com.example.ui.MainTrackerScreen
import com.example.ui.TrackerViewModel
import com.example.ui.TrackerViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize local Room SQLite Database and Repository
        val database = TrackerDatabase.getDatabase(this)
        val repository = TrackerRepository(
            projectDao = database.projectDao(),
            expenseDao = database.expenseDao(),
            investorDao = database.investorDao(),
            chatMessageDao = database.chatMessageDao(),
            historyLogDao = database.historyLogDao()
        )

        // 2. Initialize ViewModel with modern Factory
        val viewModel: TrackerViewModel by viewModels {
            TrackerViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MainTrackerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
