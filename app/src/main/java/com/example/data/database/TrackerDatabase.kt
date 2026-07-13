package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ProjectDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.InvestorDao
import com.example.data.model.Project
import com.example.data.model.Expense
import com.example.data.model.Investor
import com.example.data.model.ChatMessage
import com.example.data.model.HistoryLog
import com.example.data.dao.ChatMessageDao
import com.example.data.dao.HistoryLogDao

@Database(
    entities = [Project::class, Expense::class, Investor::class, ChatMessage::class, HistoryLog::class],
    version = 4,
    exportSchema = false
)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun investorDao(): InvestorDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun historyLogDao(): HistoryLogDao

    companion object {
        @Volatile
        private var INSTANCE: TrackerDatabase? = null

        fun getDatabase(context: Context): TrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
