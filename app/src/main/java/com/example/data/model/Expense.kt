package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val projectName: String,
    val amount: Double,
    val category: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending", // Pending, Approved, Rejected
    val submittedBy: String,
    val approvedBy: String? = null,
    val witnessApprovals: String = "", // Comma-separated list of witness names who approved
    val witnessRejections: String = "", // Comma-separated list of witness names who rejected
    val receiptImage: String? = "img_receipt_sample" // Name of drawable or upload placeholder
)
