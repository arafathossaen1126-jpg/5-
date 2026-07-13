package com.example.data.dao

import androidx.room.*
import com.example.data.model.Project
import com.example.data.model.Expense
import com.example.data.model.Investor
import com.example.data.model.ChatMessage
import com.example.data.model.HistoryLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Query("UPDATE projects SET currentSpend = currentSpend + :amount WHERE id = :projectId")
    suspend fun addProjectSpend(projectId: Int, amount: Double)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE status = 'Pending' ORDER BY timestamp DESC")
    fun getPendingExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("UPDATE expenses SET status = :status, approvedBy = :approvedBy WHERE id = :id")
    suspend fun updateExpenseStatus(id: Int, status: String, approvedBy: String?)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Int)
}

@Dao
interface InvestorDao {
    @Query("SELECT * FROM investors ORDER BY totalInvestment DESC")
    fun getAllInvestors(): Flow<List<Investor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestor(investor: Investor)

    @Query("DELETE FROM investors WHERE id = :id")
    suspend fun deleteInvestorById(id: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("UPDATE chat_messages SET isSynced = 1 WHERE isSynced = 0")
    suspend fun syncAllMessages()

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Dao
interface HistoryLogDao {
    @Query("SELECT * FROM history_logs ORDER BY timestamp DESC")
    fun getAllHistoryLogs(): Flow<List<HistoryLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryLog(log: HistoryLog)

    @Query("DELETE FROM history_logs")
    suspend fun clearHistoryLogs()
}
