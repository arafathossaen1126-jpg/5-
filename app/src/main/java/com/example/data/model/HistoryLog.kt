package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_logs")
data class HistoryLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actionType: String, // "Investment", "Expense Submission", "Expense Approval", "Expense Rejection", "Withdrawal", "Project Added", "Project Deleted"
    val memberName: String,
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
