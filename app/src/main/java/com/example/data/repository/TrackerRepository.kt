package com.example.data.repository

import com.example.data.dao.ProjectDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.InvestorDao
import com.example.data.dao.ChatMessageDao
import com.example.data.dao.HistoryLogDao
import com.example.data.model.Project
import com.example.data.model.Expense
import com.example.data.model.Investor
import com.example.data.model.ChatMessage
import com.example.data.model.HistoryLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TrackerRepository(
    private val projectDao: ProjectDao,
    private val expenseDao: ExpenseDao,
    private val investorDao: InvestorDao,
    private val chatMessageDao: ChatMessageDao,
    private val historyLogDao: HistoryLogDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val pendingExpenses: Flow<List<Expense>> = expenseDao.getPendingExpenses()
    val allInvestors: Flow<List<Investor>> = investorDao.getAllInvestors()
    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()
    val allHistoryLogs: Flow<List<HistoryLog>> = historyLogDao.getAllHistoryLogs()

    suspend fun insertProject(project: Project) {
        projectDao.insertProject(project)
    }

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
        // If the expense is pre-approved, immediately update the project spend
        if (expense.status == "Approved") {
            projectDao.addProjectSpend(expense.projectId, expense.amount)
        }
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
        if (expense.status == "Approved") {
            projectDao.addProjectSpend(expense.projectId, expense.amount)
        }
    }

    suspend fun approveExpense(expenseId: Int, projectId: Int, amount: Double, approvedBy: String) {
        expenseDao.updateExpenseStatus(expenseId, "Approved", approvedBy)
        projectDao.addProjectSpend(projectId, amount)
    }

    suspend fun rejectExpense(expenseId: Int) {
        expenseDao.updateExpenseStatus(expenseId, "Rejected", null)
    }

    suspend fun insertInvestor(investor: Investor) {
        investorDao.insertInvestor(investor)
    }

    suspend fun insertMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun syncAllMessages() {
        chatMessageDao.syncAllMessages()
    }

    suspend fun clearChat() {
        chatMessageDao.clearChat()
    }

    suspend fun insertHistoryLog(log: HistoryLog) {
        historyLogDao.insertHistoryLog(log)
    }

    suspend fun clearHistoryLogs() {
        historyLogDao.clearHistoryLogs()
    }

    suspend fun deleteProject(id: Int) {
        projectDao.deleteProjectById(id)
    }

    suspend fun deleteExpense(id: Int) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun deleteInvestor(id: Int) {
        investorDao.deleteInvestorById(id)
    }

    // Seeds the database with rich Agro-themed starter data if it is empty.
    suspend fun seedDatabaseIfEmpty() {
        val projects = allProjects.first()
        if (projects.isEmpty()) {
            // Seed Projects
            val initialProjects = listOf(
                Project(
                    name = "Greenhouse Hydroponics",
                    description = "Advanced automated water-based vegetable greenhouse.",
                    budget = 150000.0,
                    currentSpend = 42000.0,
                    startDate = "2026-05-10",
                    status = "Active"
                ),
                Project(
                    name = "Organic Dairy Farm",
                    description = "Eco-friendly pasteurization & milk distribution plant.",
                    budget = 250000.0,
                    currentSpend = 120000.0,
                    startDate = "2026-04-01",
                    status = "Active"
                ),
                Project(
                    name = "Smart Drip Irrigation",
                    description = "Solar-powered micro-irrigation system for orchard fields.",
                    budget = 75000.0,
                    currentSpend = 15000.0,
                    startDate = "2026-06-15",
                    status = "Active"
                ),
                Project(
                    name = "Bio-Fertilizer Unit",
                    description = "Composting and nutrient production facility.",
                    budget = 40000.0,
                    currentSpend = 0.0,
                    startDate = "2026-07-01",
                    status = "Proposed"
                )
            )
            for (p in initialProjects) {
                projectDao.insertProject(p)
            }

            // Seed Investors (6 partners with specific roles)
            val initialInvestors = listOf(
                Investor(
                    name = "Abdur Rahman",
                    email = "abdur@agroholdings.com",
                    phone = "01764842883",
                    totalInvestment = 180000.0,
                    sharePercentage = 30.0,
                    joinedDate = "2026-01-10",
                    withdrawals = 15000.0,
                    role = "Super Admin",
                    password = "Rasel@857711"
                ),
                Investor(
                    name = "Zayan Malik",
                    email = "zayan@capitalagro.com",
                    phone = "+880 1819-876543",
                    totalInvestment = 120000.0,
                    sharePercentage = 20.0,
                    joinedDate = "2026-02-15",
                    withdrawals = 8000.0,
                    role = "Witness",
                    password = "123456"
                ),
                Investor(
                    name = "Nusrat Jahan",
                    email = "nusrat@greenventures.org",
                    phone = "+880 1912-987123",
                    totalInvestment = 100000.0,
                    sharePercentage = 15.0,
                    joinedDate = "2026-03-20",
                    withdrawals = 5000.0,
                    role = "Witness",
                    password = "123456"
                ),
                Investor(
                    name = "Farhana Yasmin",
                    email = "farhana@agroholdings.com",
                    phone = "+880 1515-456789",
                    totalInvestment = 90000.0,
                    sharePercentage = 15.0,
                    joinedDate = "2026-04-05",
                    withdrawals = 4000.0,
                    role = "Witness",
                    password = "123456"
                ),
                Investor(
                    name = "Rafiqul Islam",
                    email = "rafiq@capitalagro.com",
                    phone = "+880 1616-987654",
                    totalInvestment = 80000.0,
                    sharePercentage = 12.0,
                    joinedDate = "2026-04-12",
                    withdrawals = 3000.0,
                    role = "General Investor",
                    password = "123456"
                ),
                Investor(
                    name = "Tasnim Ahmed",
                    email = "tasnim@greenventures.org",
                    phone = "+880 1313-112233",
                    totalInvestment = 50000.0,
                    sharePercentage = 8.0,
                    joinedDate = "2026-05-18",
                    withdrawals = 1000.0,
                    role = "General Investor",
                    password = "123456"
                )
            )
            for (i in initialInvestors) {
                investorDao.insertInvestor(i)
                historyLogDao.insertHistoryLog(
                    HistoryLog(
                        actionType = "Investment",
                        memberName = i.name,
                        amount = i.totalInvestment,
                        description = "Initial share capital deposit of ৳${String.format("%,.0f", i.totalInvestment)} at ${i.sharePercentage}% share."
                    )
                )
                if (i.withdrawals > 0) {
                    historyLogDao.insertHistoryLog(
                        HistoryLog(
                            actionType = "Withdrawal",
                            memberName = i.name,
                            amount = i.withdrawals,
                            description = "Approved capital withdrawal of ৳${String.format("%,.0f", i.withdrawals)}."
                        )
                    )
                }
            }

            // Seed Projects
            for (p in initialProjects) {
                historyLogDao.insertHistoryLog(
                    HistoryLog(
                        actionType = "Project Added",
                        memberName = "System",
                        amount = p.budget,
                        description = "New agricultural project initiated: '${p.name}' with budget ৳${String.format("%,.0f", p.budget)}."
                    )
                )
            }

            // Seed Some Expenses (We'll seed some as approved, and some as pending for approval testing)
            val initialExpenses = listOf(
                Expense(
                    projectId = 1, // Greenhouse
                    projectName = "Greenhouse Hydroponics",
                    amount = 25000.0,
                    category = "Equipment",
                    description = "pH sensors, water pumps, and PVC grow pipes.",
                    status = "Approved",
                    submittedBy = "Abdur Rahman",
                    approvedBy = "System"
                ),
                Expense(
                    projectId = 1, // Greenhouse
                    projectName = "Greenhouse Hydroponics",
                    amount = 17000.0,
                    category = "Seeds & Nutrients",
                    description = "Premium Dutch tomato seeds & liquid fertilizer stock.",
                    status = "Approved",
                    submittedBy = "Zayan Malik",
                    approvedBy = "System"
                ),
                Expense(
                    projectId = 2, // Dairy
                    projectName = "Organic Dairy Farm",
                    amount = 120000.0,
                    category = "Machinery",
                    description = "Milk homogenizer and high-capacity bottle-filling line.",
                    status = "Approved",
                    submittedBy = "Nusrat Jahan",
                    approvedBy = "System"
                ),
                // Pending Expenses for approvals review tab
                Expense(
                    projectId = 1,
                    projectName = "Greenhouse Hydroponics",
                    amount = 6000.0,
                    category = "Utility",
                    description = "Power substation upgrade and backup generator lease.",
                    status = "Pending",
                    submittedBy = "Zayan Malik"
                ),
                Expense(
                    projectId = 3,
                    projectName = "Smart Drip Irrigation",
                    amount = 15000.0,
                    category = "Labor",
                    description = "Orchard trenching and pipe layout workforce wages.",
                    status = "Pending",
                    submittedBy = "Nusrat Jahan"
                )
            )
            for (e in initialExpenses) {
                expenseDao.insertExpense(e)
                historyLogDao.insertHistoryLog(
                    HistoryLog(
                        actionType = "Expense Submission",
                        memberName = e.submittedBy,
                        amount = e.amount,
                        description = "Submitted ৳${String.format("%,.0f", e.amount)} expense for '${e.projectName}' under ${e.category}."
                    )
                )
                if (e.status == "Approved") {
                    historyLogDao.insertHistoryLog(
                        HistoryLog(
                            actionType = "Expense Approval",
                            memberName = e.approvedBy ?: "System",
                            amount = e.amount,
                            description = "Approved expense of ৳${String.format("%,.0f", e.amount)} for '${e.projectName}'."
                        )
                    )
                }
            }
        }
    }
}
