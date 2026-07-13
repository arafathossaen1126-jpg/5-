package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val budget: Double,
    val currentSpend: Double = 0.0,
    val startDate: String,
    val status: String = "Active" // Active, Completed, Proposed
)
