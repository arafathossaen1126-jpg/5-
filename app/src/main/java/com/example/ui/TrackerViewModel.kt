package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Project
import com.example.data.model.Expense
import com.example.data.model.Investor
import com.example.data.model.ChatMessage
import com.example.data.model.HistoryLog
import com.example.data.repository.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackerViewModel(private val repository: TrackerRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingExpenses: StateFlow<List<Expense>> = repository.pendingExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val investors: StateFlow<List<Investor>> = repository.allInvestors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val chatMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyLogs: StateFlow<List<HistoryLog>> = repository.allHistoryLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOnlineStatus(online: Boolean) {
        _isOnline.value = online
        if (online) {
            viewModelScope.launch {
                repository.syncAllMessages()
            }
        }
    }

    private val _selectedInvestor = MutableStateFlow<Investor?>(null)
    val selectedInvestor: StateFlow<Investor?> = combine(investors, _selectedInvestor) { investorList, selected ->
        selected ?: investorList.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun selectInvestor(investor: Investor) {
        _selectedInvestor.value = investor
    }

    fun createProject(name: String, description: String, budget: Double, startDate: String) {
        viewModelScope.launch {
            repository.insertProject(
                Project(
                    name = name,
                    description = description,
                    budget = budget,
                    startDate = startDate,
                    status = "Active"
                )
            )
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Project Added",
                    memberName = _selectedInvestor.value?.name ?: "System",
                    amount = budget,
                    description = "Created project '$name' with budget ৳${String.format("%,.0f", budget)}."
                )
            )
        }
    }

    fun submitExpense(projectId: Int, projectName: String, amount: Double, category: String, description: String, submittedBy: String) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    projectId = projectId,
                    projectName = projectName,
                    amount = amount,
                    category = category,
                    description = description,
                    status = "Pending",
                    submittedBy = submittedBy
                )
            )
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Expense Submission",
                    memberName = submittedBy,
                    amount = amount,
                    description = "Submitted ৳${String.format("%,.0f", amount)} expense for '$projectName' ($category): $description."
                )
            )
        }
    }

    fun approvePendingExpense(expenseId: Int, projectId: Int, amount: Double, approvedBy: String) {
        viewModelScope.launch {
            repository.approveExpense(expenseId, projectId, amount, approvedBy)
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Expense Approval",
                    memberName = approvedBy,
                    amount = amount,
                    description = "Directly approved ৳${String.format("%,.0f", amount)} expense ID $expenseId."
                )
            )
        }
    }

    fun rejectPendingExpense(expenseId: Int) {
        viewModelScope.launch {
            repository.rejectExpense(expenseId)
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Expense Rejection",
                    memberName = _selectedInvestor.value?.name ?: "Admin",
                    amount = 0.0,
                    description = "Rejected pending expense ID $expenseId."
                )
            )
        }
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Project Deleted",
                    memberName = _selectedInvestor.value?.name ?: "Admin",
                    amount = 0.0,
                    description = "Deleted project ID $projectId."
                )
            )
        }
    }

    fun voteExpenseApproval(expense: Expense, voterName: String, voterRole: String, approve: Boolean) {
        viewModelScope.launch {
            if (voterRole == "Super Admin") {
                if (approve) {
                    repository.approveExpense(expense.id, expense.projectId, expense.amount, voterName)
                    repository.insertHistoryLog(
                        HistoryLog(
                            actionType = "Expense Approval",
                            memberName = voterName,
                            amount = expense.amount,
                            description = "Super Admin approved ৳${String.format("%,.0f", expense.amount)} expense for '${expense.projectName}' (${expense.category})."
                        )
                    )
                } else {
                    repository.rejectExpense(expense.id)
                    repository.insertHistoryLog(
                        HistoryLog(
                            actionType = "Expense Rejection",
                            memberName = voterName,
                            amount = expense.amount,
                            description = "Super Admin rejected ৳${String.format("%,.0f", expense.amount)} expense for '${expense.projectName}'."
                        )
                    )
                }
                return@launch
            }

            if (voterRole == "Witness") {
                val approvalsList = expense.witnessApprovals.split(",").filter { it.isNotBlank() }.toMutableList()
                val rejectionsList = expense.witnessRejections.split(",").filter { it.isNotBlank() }.toMutableList()

                if (approve) {
                    if (!approvalsList.contains(voterName)) {
                        approvalsList.add(voterName)
                        rejectionsList.remove(voterName)
                    }
                } else {
                    if (!rejectionsList.contains(voterName)) {
                        rejectionsList.add(voterName)
                        approvalsList.remove(voterName)
                    }
                }

                val updatedApprovals = approvalsList.joinToString(",")
                val updatedRejections = rejectionsList.joinToString(",")

                if (approvalsList.size >= 3) {
                    val updatedExpense = expense.copy(
                        witnessApprovals = updatedApprovals,
                        witnessRejections = updatedRejections,
                        status = "Approved",
                        approvedBy = approvalsList.joinToString(", ")
                    )
                    repository.updateExpense(updatedExpense)
                    repository.insertHistoryLog(
                        HistoryLog(
                            actionType = "Expense Approval",
                            memberName = voterName,
                            amount = expense.amount,
                            description = "Expense of ৳${String.format("%,.0f", expense.amount)} for '${expense.projectName}' fully approved by witnesses: ${approvalsList.joinToString(", ")}."
                        )
                    )
                } else if (rejectionsList.size >= 2) {
                    val updatedExpense = expense.copy(
                        witnessApprovals = updatedApprovals,
                        witnessRejections = updatedRejections,
                        status = "Rejected"
                    )
                    repository.updateExpense(updatedExpense)
                    repository.insertHistoryLog(
                        HistoryLog(
                            actionType = "Expense Rejection",
                            memberName = voterName,
                            amount = expense.amount,
                            description = "Expense of ৳${String.format("%,.0f", expense.amount)} for '${expense.projectName}' rejected by witnesses: ${rejectionsList.joinToString(", ")}."
                        )
                    )
                } else {
                    val updatedExpense = expense.copy(
                        witnessApprovals = updatedApprovals,
                        witnessRejections = updatedRejections
                    )
                    repository.updateExpense(updatedExpense)
                    repository.insertHistoryLog(
                        HistoryLog(
                            actionType = "Witness Vote",
                            memberName = voterName,
                            amount = expense.amount,
                            description = "Witness ${voterName} voted to ${if (approve) "approve" else "reject"} '${expense.description}' (৳${String.format("%,.0f", expense.amount)}). Progress: ${approvalsList.size}/3 approved, ${rejectionsList.size}/2 rejected."
                        )
                    )
                }
            }
        }
    }

    fun sendMessage(sender: String, messageText: String) {
        viewModelScope.launch {
            repository.insertMessage(
                ChatMessage(
                    sender = sender,
                    message = messageText,
                    isSynced = _isOnline.value
                )
            )
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun createInvestor(name: String, email: String, phone: String, totalInvestment: Double, sharePercentage: Double, role: String, withdrawals: Double = 0.0) {
        viewModelScope.launch {
            repository.insertInvestor(
                Investor(
                    name = name,
                    email = email,
                    phone = phone,
                    totalInvestment = totalInvestment,
                    sharePercentage = sharePercentage,
                    joinedDate = "2026-06-27",
                    withdrawals = withdrawals,
                    role = role
                )
            )
            repository.insertHistoryLog(
                HistoryLog(
                    actionType = "Investment",
                    memberName = name,
                    amount = totalInvestment,
                    description = "Logged partner '${name}' with ৳${String.format("%,.0f", totalInvestment)} investment at ${sharePercentage}% share."
                )
            )
        }
    }

    fun updateInvestor(investor: Investor) {
        viewModelScope.launch {
            repository.insertInvestor(investor)
            // If active investor, update selected value
            if (_selectedInvestor.value?.id == investor.id) {
                _selectedInvestor.value = investor
            }
        }
    }
}

class TrackerViewModelFactory(private val repository: TrackerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
