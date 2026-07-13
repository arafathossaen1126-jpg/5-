package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investors")
data class Investor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val totalInvestment: Double,
    val sharePercentage: Double,
    val joinedDate: String,
    val withdrawals: Double = 0.0,
    val role: String = "General Investor", // Super Admin, Witness, General Investor
    val password: String = "123456"
)
