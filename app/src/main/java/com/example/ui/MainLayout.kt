package com.example.ui

import com.example.R
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Project
import com.example.data.model.Expense
import com.example.data.model.Investor
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab {
    Dashboard,
    Investors,
    Chat,
    Approvals,
    Settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTrackerScreen(viewModel: TrackerViewModel) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val pendingExpenses by viewModel.pendingExpenses.collectAsStateWithLifecycle()
    val investors by viewModel.investors.collectAsStateWithLifecycle()
    val activeInvestor by viewModel.selectedInvestor.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val historyLogs by viewModel.historyLogs.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.Dashboard) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showProfileSelector by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var isBengali by remember { mutableStateOf(true) }
    var hasLoggedIn by rememberSaveable { mutableStateOf(false) }

    // Localization maps
    val t = remember(isBengali) {
        if (isBengali) {
            mapOf(
                "title" to "মাল্টি-প্রজেক্ট ট্র্যাকার",
                "dashboard" to "ড্যাশবোর্ড",
                "investors" to "বিনিয়োগকারী",
                "approvals" to "অনুমোদন",
                "settings" to "সেটিংস",
                "welcome" to "স্বাগতম!",
                "member" to "সদস্য",
                "change_member" to "মেম্বার পরিবর্তন",
                "sync_active" to "স্বয়ংক্রিয় অফলাইন সিঙ্ক সচল (Room)",
                "sync_desc" to "ইন্টারনেট না থাকলেও অ্যাপের সব ডাটা ডিভাইসে লোকাল SQLite ডাটাবেজে জমা থাকবে এবং নেটওয়ার্ক সচল হওয়ামাত্র স্বয়ংক্রিয়ভাবে সকলের মোবাইল সিঙ্ক হবে।",
                "net_profit" to "নিট মুনাফা",
                "total_budget" to "মোট আয়",
                "total_spend" to "মোট খরচ",
                "total_loss" to "মোট লোকসান",
                "total_due" to "মোট বকেয়া",
                "cash_in_hand" to "হাতে নগদ",
                "compare_title" to "আয় ও ব্যয়ের তুলনা চিত্র",
                "spent_ratio" to "ব্যয়ের অনুপাত",
                "projects_list" to "প্রজেক্টের তালিকা",
                "budget_label" to "বাজেট",
                "spent_label" to "ব্যয়",
                "add_title" to "নতুন প্রজেক্ট বা ব্যয় যুক্ত করুন",
                "add_project" to "নতুন প্রজেক্ট",
                "add_expense" to "নতুন ব্যয়",
                "submit" to "জমা দিন",
                "cancel" to "বাতিল",
                "project_name" to "প্রজেক্টের নাম",
                "description" to "বিবরণ",
                "budget_input" to "বাজেট (৳)",
                "start_date" to "শুরুর তারিখ",
                "amount_input" to "পরিমাণ (৳)",
                "category" to "ক্যাটাগরি",
                "select_project" to "প্রজেক্ট নির্বাচন করুন",
                "pending_reviews" to "অনুমোদন অপেক্ষমাণ তালিকা",
                "submitted_by" to "দাখিলকারী",
                "approve" to "অনুমোদন",
                "reject" to "বাতিল",
                "investor_profiles" to "বিনিয়োগকারীদের প্রোফাইল",
                "total_invested" to "মোট বিনিয়োগ",
                "share" to "অংশীদারিত্ব",
                "add_investor" to "নতুন বিনিয়োগকারী",
                "phone" to "ফোন",
                "email" to "ইমেইল",
                "stats" to "ডাটাবেজ পরিসংখ্যান",
                "db_projects" to "মোট প্রজেক্ট",
                "db_expenses" to "মোট ব্যয় এন্ট্রি",
                "db_investors" to "মোট বিনিয়োগকারী",
                "language" to "ভাষা পরিবর্তন",
                "withdrawals" to "উত্তোলন",
                "image_upload" to "ছবি আপলোড করুন (ঐচ্ছিক)",
                "reset_db" to "ডাটাবেজ রিসেট করুন",
                "chat" to "চ্যাট রুম",
                "online" to "অনলাইন",
                "offline" to "অফলাইন মোড",
                "go_online" to "অনলাইন মোডে যান",
                "sync_now" to "ডাটা সিঙ্ক করুন",
                "offline_mode" to "অফলাইন মোড (ডিভাইস ক্যাশে সচল)",
                "online_desc" to "সকল ডাটা ইনস্ট্যান্ট সিঙ্ক হচ্ছে",
                "vote_approved" to "অনুমোদন ভোট দিন",
                "vote_rejected" to "বাতিল ভোট দিন",
                "approved_by" to "অনুমোদনকারী",
                "witness_progress" to "সাক্ষী স্বাক্ষর অগ্রগতি",
                "super_admin_approved" to "সুপার এডমিন দ্বারা অনুমোদিত",
                "super_admin_rejected" to "সুপার এডমিন দ্বারা বাতিলকৃত",
                "witness_approved_fully" to "৩ জন সাক্ষীর অনুমোদন সম্পন্ন",
                "witness_rejected_fully" to "সাক্ষীদের প্রত্যাখ্যান সম্পন্ন",
                "pending_status" to "অপেক্ষমাণ",
                "delete_project" to "প্রজেক্ট ডিলিট",
                "delete_confirm" to "আপনি কি এই প্রজেক্ট ডিলিট করতে চান?",
                "admin_only" to "শুধুমাত্র সুপার এডমিন প্রজেক্ট ডিলিট করতে পারেন",
                "profile_login" to "বিনিয়োগকারী প্রোফাইল লগইন",
                "create_profile" to "নতুন প্রোফাইল তৈরি করুন",
                "connect_profile" to "বিদ্যমান প্রোফাইলে যুক্ত হোন",
                "select_profile_label" to "প্রোফাইল নির্বাচন করুন",
                "role_label" to "পদবি / রোল",
                "role_admin" to "সুপার এডমিন (Super Admin)",
                "role_witness" to "অনুমোদনকারী সাক্ষী (Witness)",
                "role_investor" to "সাধারণ বিনিয়োগকারী"
            )
        } else {
            mapOf(
                "title" to "Multi-Project Tracker",
                "dashboard" to "Dashboard",
                "investors" to "Investors",
                "approvals" to "Approvals",
                "settings" to "Settings",
                "welcome" to "Welcome!",
                "member" to "Member",
                "change_member" to "Change Member",
                "sync_active" to "Automatic Offline Sync Active (Room)",
                "sync_desc" to "Even without internet, all app data is securely stored in your local SQLite database and will automatically sync once a network connection is available.",
                "net_profit" to "Net Profit",
                "total_budget" to "Total Income",
                "total_spend" to "Total Expense",
                "total_loss" to "Total Loss",
                "total_due" to "Total Due",
                "cash_in_hand" to "Cash in Hand",
                "compare_title" to "Income & Expense Comparison",
                "spent_ratio" to "Spent Ratio",
                "projects_list" to "Projects List",
                "budget_label" to "Budget",
                "spent_label" to "Spent",
                "add_title" to "Add Project or Expense",
                "add_project" to "Add Project",
                "add_expense" to "Add Expense",
                "submit" to "Submit",
                "cancel" to "Cancel",
                "project_name" to "Project Name",
                "description" to "Description",
                "budget_input" to "Budget (৳)",
                "start_date" to "Start Date",
                "amount_input" to "Amount (৳)",
                "category" to "Category",
                "select_project" to "Select Project",
                "pending_reviews" to "Pending Approvals Review",
                "submitted_by" to "Submitted by",
                "approve" to "Approve",
                "reject" to "Reject",
                "investor_profiles" to "Investor Profiles",
                "total_invested" to "Total Invested",
                "share" to "Share",
                "add_investor" to "Add Investor",
                "phone" to "Phone",
                "email" to "Email",
                "stats" to "Database Stats",
                "db_projects" to "Total Projects",
                "db_expenses" to "Total Expenses",
                "db_investors" to "Total Investors",
                "language" to "Switch Language",
                "withdrawals" to "Withdrawals",
                "image_upload" to "Upload Image (Optional)",
                "reset_db" to "Reset & Seed Database",
                "chat" to "Chat Room",
                "online" to "Online",
                "offline" to "Offline Mode",
                "go_online" to "Go Online Mode",
                "sync_now" to "Sync Offline Data",
                "offline_mode" to "Offline Mode (Local Cache Active)",
                "online_desc" to "Real-time updates are active",
                "vote_approved" to "Vote Approve",
                "vote_rejected" to "Vote Reject",
                "approved_by" to "Approved by",
                "witness_progress" to "Witness Approvals Signatures",
                "super_admin_approved" to "Super Admin Approved",
                "super_admin_rejected" to "Super Admin Rejected",
                "witness_approved_fully" to "Approved by 3 Witnesses",
                "witness_rejected_fully" to "Rejected by Witnesses",
                "pending_status" to "Pending",
                "delete_project" to "Delete Project",
                "delete_confirm" to "Are you sure you want to delete this project?",
                "admin_only" to "Only Super Admin is authorized to delete projects",
                "profile_login" to "Investor Profile Login",
                "create_profile" to "Create New Profile",
                "connect_profile" to "Connect to Existing Partner",
                "select_profile_label" to "Select User Profile",
                "role_label" to "Authority Role",
                "role_admin" to "Super Admin",
                "role_witness" to "Witness Approver",
                "role_investor" to "General Investor"
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasLoggedIn) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(LightBg, SoftMint)
                        )
                    )
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Agro Theme Logo Placeholder
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DeepGreen)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "Agro Holdings Logo",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isBengali) "এগ্রো হোল্ডিংস ট্র্যাকার" else "Agro Holdings Tracker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isBengali) "অংশীদারদের সমন্বিত রিয়েল-টাইম ডাটাবেজ পোর্টাল" else "Integrated Real-time Partner Portal",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                var showForgetPasswordDialog by remember { mutableStateOf(false) }

                // Authentication Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = LightSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    var loginModeExisting by remember { mutableStateOf(true) }
                    var phoneInput by remember { mutableStateOf("") }
                    var passwordInput by remember { mutableStateOf("") }
                    var registerName by remember { mutableStateOf("") }
                    var registerEmail by remember { mutableStateOf("") }
                    var registerPhone by remember { mutableStateOf("") }
                    var registerCapital by remember { mutableStateOf("") }
                    var registerPassword by remember { mutableStateOf("") }
                    var registerRole by remember { mutableStateOf("General Investor") }
                    var errorMessage by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Language switcher inside login
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isBengali = !isBengali }) {
                                Text(
                                    text = if (isBengali) "English" else "বাংলা",
                                    color = DeepGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Tab Selection: Sign In / Register
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(LightBg)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (loginModeExisting) SoftMint else Color.Transparent)
                                    .clickable { 
                                        loginModeExisting = true
                                        errorMessage = ""
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBengali) "লগইন" else "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (loginModeExisting) ForestGreen else TextMuted
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!loginModeExisting) SoftMint else Color.Transparent)
                                    .clickable { 
                                        loginModeExisting = false
                                        errorMessage = ""
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isBengali) "রেজিস্ট্রেশন" else "Register",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (!loginModeExisting) ForestGreen else TextMuted
                                )
                            }
                        }

                        if (loginModeExisting) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { 
                                    phoneInput = it
                                    errorMessage = ""
                                },
                                label = { Text(if (isBengali) "ফোন নম্বর" else "Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth().testTag("input_login_phone")
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { 
                                    passwordInput = it
                                    errorMessage = ""
                                },
                                label = { Text(if (isBengali) "পাসওয়ার্ড" else "Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DeepGreen) },
                                visualTransformation = PasswordVisualTransformation(),
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth().testTag("input_login_password")
                            )

                            if (errorMessage.isNotBlank()) {
                                Text(
                                    text = errorMessage,
                                    color = AlertRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showForgetPasswordDialog = true }) {
                                    Text(
                                        text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                                        color = DeepGreen,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val trimmedPhone = phoneInput.trim()
                                    val trimmedPassword = passwordInput.trim()

                                    if (trimmedPhone.isBlank() || trimmedPassword.isBlank()) {
                                        errorMessage = if (isBengali) "অনুগ্রহ করে সব তথ্য দিন।" else "Please fill in all fields."
                                        return@Button
                                    }

                                    // Check if it matches Super Admin hardcoded default
                                    if (trimmedPhone == "01764842883" && trimmedPassword == "Rasel@857711") {
                                        // Find Abdur Rahman profile to select
                                        val superAdmin = investors.find { it.role == "Super Admin" }
                                        if (superAdmin != null) {
                                            viewModel.selectInvestor(superAdmin)
                                        }
                                        hasLoggedIn = true
                                    } else {
                                        // Match in database of investors
                                        val user = investors.find { it.phone.replace(" ", "").contains(trimmedPhone) || trimmedPhone.contains(it.phone.replace(" ", "").replace("+88", "")) }
                                        if (user != null) {
                                            if (user.password == trimmedPassword) {
                                                viewModel.selectInvestor(user)
                                                hasLoggedIn = true
                                            } else {
                                                errorMessage = if (isBengali) "ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।" else "Incorrect password. Try again."
                                            }
                                        } else {
                                            errorMessage = if (isBengali) "এই নম্বরে কোনো অংশীদার পাওয়া যায়নি।" else "No partner found with this number."
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                                modifier = Modifier.fillMaxWidth().testTag("btn_login_submit"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "লগইন করুন" else "Log In",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Quick Demo Access (Polished and extremely helpful for testing)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "ডেমো কুইক লগইন (পরীক্ষার জন্য):" else "Quick Demo Logins (For Testing):",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                investors.take(3).forEach { investor ->
                                    val fillPhone = if (investor.role == "Super Admin") "01764842883" else investor.phone
                                    val fillPass = if (investor.role == "Super Admin") "Rasel@857711" else investor.password
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(LightBg)
                                            .clickable {
                                                phoneInput = fillPhone
                                                passwordInput = fillPass
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${investor.name} (${investor.role})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextDark
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = DeepGreen
                                        )
                                    }
                                }
                            }
                        } else {
                            // Register screen
                            OutlinedTextField(
                                value = registerName,
                                onValueChange = { registerName = it },
                                label = { Text(if (isBengali) "পূর্ণ নাম" else "Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = registerEmail,
                                onValueChange = { registerEmail = it },
                                label = { Text(if (isBengali) "ইমেইল এড্রেস" else "Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = registerPhone,
                                onValueChange = { registerPhone = it },
                                label = { Text(if (isBengali) "ফোন নম্বর" else "Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = registerCapital,
                                onValueChange = { registerCapital = it },
                                label = { Text(if (isBengali) "বিনিয়োগ মূলধন (৳)" else "Investment Capital (৳)") },
                                leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = registerPassword,
                                onValueChange = { registerPassword = it },
                                label = { Text(if (isBengali) "নতুন পাসওয়ার্ড" else "Choose Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DeepGreen) },
                                colors = defaultTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = if (isBengali) "রোল / পদবি নির্বাচন করুন:" else "Select Authority Role:",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf("Super Admin", "Witness", "General Investor").forEach { roleName ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (registerRole == roleName) SoftMint else LightBg)
                                            .border(1.dp, if (registerRole == roleName) DeepGreen else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { registerRole = roleName }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isBengali) {
                                                when(roleName) {
                                                    "Super Admin" -> "এডমিন"
                                                    "Witness" -> "সাক্ষী"
                                                    else -> "বিনিয়োগকারী"
                                                }
                                            } else roleName.replace("General ", ""),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (registerRole == roleName) ForestGreen else TextMuted
                                        )
                                    }
                                }
                            }

                            if (errorMessage.isNotBlank()) {
                                Text(
                                    text = errorMessage,
                                    color = AlertRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Button(
                                onClick = {
                                    if (registerName.isBlank() || registerPhone.isBlank() || registerPassword.isBlank()) {
                                        errorMessage = if (isBengali) "অনুগ্রহ করে নাম, ফোন ও পাসওয়ার্ড লিখুন।" else "Please provide Name, Phone and Password."
                                        return@Button
                                    }
                                    val capVal = registerCapital.toDoubleOrNull() ?: 0.0
                                    val newlyRegistered = Investor(
                                        name = registerName,
                                        email = registerEmail,
                                        phone = registerPhone,
                                        totalInvestment = capVal,
                                        sharePercentage = 12.0,
                                        joinedDate = "2026-06-27",
                                        withdrawals = 0.0,
                                        role = registerRole,
                                        password = registerPassword
                                    )
                                    viewModel.updateInvestor(newlyRegistered)
                                    viewModel.selectInvestor(newlyRegistered)
                                    hasLoggedIn = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "অ্যাকাউন্ট তৈরি ও লগইন" else "Register & Log In",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (showForgetPasswordDialog) {
                    Dialog(onDismissRequest = { showForgetPasswordDialog = false }) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = LightSurface),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "পাসওয়ার্ড পুনরুদ্ধার" else "Password Reset Support",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DeepGreen
                                )
                                Text(
                                    text = if (isBengali) "নিরাপত্তা স্বার্থে, আপনার পাসওয়ার্ড রিসেট করতে বা পুনরুদ্ধার করতে অনুগ্রহ করে এগ্রো হোল্ডিংস সুপার এডমিনের সাথে যোগাযোগ করুন।" 
                                           else "For security reasons, please contact the Agro Holdings Super Admin to reset or retrieve your password.",
                                    fontSize = 13.sp,
                                    color = TextDark,
                                    textAlign = TextAlign.Center
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftMint)
                                        .padding(12.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = if (isBengali) "সুপার এডমিন হটলাইন:" else "Super Admin Helpline:", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                        Text(text = "01764842883", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                                    }
                                }
                                Button(
                                    onClick = { showForgetPasswordDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isBengali) "ঠিক আছে" else "Close")
                                }
                            }
                        }
                    }
                }
            }
        }
        Scaffold(
            topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star Logo",
                            tint = DeepGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = t["title"] ?: "Multi-Project Tracker",
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    // Network Connectivity Status Pill
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("connectivity_status_pill")
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isOnline) SoftMint else AlertRedBg)
                            .clickable { viewModel.setOnlineStatus(!isOnline) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) ForestGreen else AlertRed)
                            )
                            Text(
                                text = if (isOnline) "Online" else "Offline",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) ForestGreen else AlertRed
                            )
                        }
                    }

                    // Top App Bar History Button (A beautiful green badge icon button)
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("top_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History Log",
                            tint = DeepGreen
                        )
                    }

                    // Top Right User Profile Selector Badge
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("top_profile_badge")
                            .clip(RoundedCornerShape(20.dp))
                            .background(SoftMint)
                            .clickable { showProfileSelector = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(DeepGreen)
                            )
                            Text(
                                text = activeInvestor?.name ?: "Guest",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForestGreen
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch",
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBg
                )
            )
        },
        bottomBar = {
            // Fixed Bottom Navigation Bar with Prominent central '+' button
            BottomAppBar(
                containerColor = LightBg,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Dashboard
                    IconButton(
                        onClick = { currentTab = AppTab.Dashboard },
                        modifier = Modifier
                            .testTag("tab_dashboard")
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (currentTab == AppTab.Dashboard) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = t["dashboard"],
                                tint = if (currentTab == AppTab.Dashboard) DeepGreen else TextMuted
                            )
                            Text(
                                text = t["dashboard"] ?: "",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == AppTab.Dashboard) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == AppTab.Dashboard) DeepGreen else TextMuted
                            )
                        }
                    }

                    // Tab 2: Investor Profiles
                    IconButton(
                        onClick = { currentTab = AppTab.Investors },
                        modifier = Modifier
                            .testTag("tab_investors")
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (currentTab == AppTab.Investors) Icons.Filled.People else Icons.Outlined.People,
                                contentDescription = t["investors"],
                                tint = if (currentTab == AppTab.Investors) DeepGreen else TextMuted
                            )
                            Text(
                                text = t["investors"] ?: "",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == AppTab.Investors) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == AppTab.Investors) DeepGreen else TextMuted
                            )
                        }
                    }

                    // Central Prominent '+' Floating Action Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(AccentGreen, DeepGreen)
                                    )
                                )
                                .clickable { showAddDialog = true }
                                .testTag("btn_prominent_add"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = t["add_title"],
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Tab: Chat Room
                    IconButton(
                        onClick = { currentTab = AppTab.Chat },
                        modifier = Modifier
                            .testTag("tab_chat")
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (currentTab == AppTab.Chat) Icons.Filled.Chat else Icons.Outlined.Chat,
                                contentDescription = t["chat"],
                                tint = if (currentTab == AppTab.Chat) DeepGreen else TextMuted
                            )
                            Text(
                                text = t["chat"] ?: "",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == AppTab.Chat) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == AppTab.Chat) DeepGreen else TextMuted
                            )
                        }
                    }

                    // Tab 3: Approval Reviews
                    IconButton(
                        onClick = { currentTab = AppTab.Approvals },
                        modifier = Modifier
                            .testTag("tab_approvals")
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box {
                                Icon(
                                    imageVector = if (currentTab == AppTab.Approvals) Icons.Filled.AssignmentTurnedIn else Icons.Outlined.AssignmentTurnedIn,
                                    contentDescription = t["approvals"],
                                    tint = if (currentTab == AppTab.Approvals) DeepGreen else TextMuted
                                )
                                if (pendingExpenses.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(AlertRed)
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                            Text(
                                text = t["approvals"] ?: "",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == AppTab.Approvals) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == AppTab.Approvals) DeepGreen else TextMuted
                            )
                        }
                    }

                    // Tab 4: Settings
                    IconButton(
                        onClick = { currentTab = AppTab.Settings },
                        modifier = Modifier
                            .testTag("tab_settings")
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (currentTab == AppTab.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = t["settings"],
                                tint = if (currentTab == AppTab.Settings) DeepGreen else TextMuted
                            )
                            Text(
                                text = t["settings"] ?: "",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == AppTab.Settings) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == AppTab.Settings) DeepGreen else TextMuted
                            )
                        }
                    }
                }
            }
        },
        containerColor = LightBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.Dashboard -> DashboardScreen(
                    projects = projects,
                    expenses = expenses,
                    investors = investors,
                    activeInvestor = activeInvestor,
                    onSwitchInvestor = { showProfileSelector = true },
                    onDeleteProject = { projectId ->
                        viewModel.deleteProject(projectId)
                    },
                    t = t,
                    isBengali = isBengali
                )
                AppTab.Investors -> InvestorsScreen(
                    investors = investors,
                    viewModel = viewModel,
                    t = t
                )
                AppTab.Chat -> ChatScreen(
                    chatMessages = chatMessages,
                    activeInvestor = activeInvestor,
                    isOnline = isOnline,
                    onSendMessage = { sender, message ->
                        viewModel.sendMessage(sender, message)
                    },
                    onToggleOnline = { status ->
                        viewModel.setOnlineStatus(status)
                    },
                    t = t
                )
                AppTab.Approvals -> ApprovalsScreen(
                    pendingExpenses = pendingExpenses,
                    activeInvestor = activeInvestor,
                    onVote = { expense, name, role, approve ->
                        viewModel.voteExpenseApproval(expense, name, role, approve)
                    },
                    t = t
                )
                AppTab.Settings -> SettingsScreen(
                    projects = projects,
                    expenses = expenses,
                    investors = investors,
                    activeInvestor = activeInvestor,
                    onUpdateInvestor = { viewModel.updateInvestor(it) },
                    onLogout = { hasLoggedIn = false },
                    isBengali = isBengali,
                    onToggleBengali = { isBengali = it },
                    onReset = {
                        // Reseed database by creating basic empty DB first
                        // ViewModel init handle reseeding
                    },
                    t = t
                )
            }
        }
    }

    // Semi-transparent scrim background overlay
    AnimatedVisibility(
        visible = showAddDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { showAddDialog = false }
        )
    }

    // Beautiful animated Bottom Sheet
    AnimatedVisibility(
        visible = showAddDialog,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .zIndex(15f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // supports edge-to-edge
                .background(Color.Transparent)
                .clickable(enabled = false) {} // prevent touch propagation
        ) {
            AddProjectExpenseBottomSheetContent(
                projects = projects,
                activeUser = activeInvestor?.name ?: "Guest",
                onSubmitProject = { name, desc, budget, date ->
                    viewModel.createProject(name, desc, budget, date)
                    showAddDialog = false
                },
                onSubmitExpense = { projectId, pName, amt, cat, desc, submitter ->
                    viewModel.submitExpense(projectId, pName, amt, cat, desc, submitter)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false },
                t = t
            )
        }
    }

    // Profile Selector Dialog
    if (showProfileSelector) {
        Dialog(onDismissRequest = { showProfileSelector = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t["change_member"] ?: "Switch User Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DeepGreen,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    if (investors.isEmpty()) {
                        CircularProgressIndicator(color = DeepGreen)
                    } else {
                        investors.forEach { investor ->
                            val isCurrent = investor.id == activeInvestor?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) SoftMint else Color.Transparent)
                                    .clickable {
                                        viewModel.selectInvestor(investor)
                                        showProfileSelector = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DeepGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = investor.name.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = investor.name,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = investor.email,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                if (isCurrent) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Current",
                                        tint = DeepGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = DeepGreen,
    unfocusedBorderColor = SoftMint,
    focusedLabelColor = DeepGreen,
    unfocusedLabelColor = TextMuted,
    cursorColor = Color.Black
)

@Composable
fun MetricGridItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        modifier = modifier
            .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    projects: List<Project>,
    expenses: List<Expense>,
    investors: List<Investor>,
    activeInvestor: Investor?,
    onSwitchInvestor: () -> Unit,
    onDeleteProject: (Int) -> Unit,
    t: Map<String, String>,
    isBengali: Boolean
) {
    // Dynamic calculations
    val totalBudget = remember(projects) { projects.sumOf { it.budget } }
    val approvedExpenses = remember(expenses) { expenses.filter { it.status == "Approved" } }
    val totalSpent = remember(approvedExpenses) { approvedExpenses.sumOf { it.amount } }
    val netProfit = totalBudget - totalSpent

    val totalLoss = remember(netProfit) { if (netProfit < 0) -netProfit else 0.0 }
    val pendingExpenses = remember(expenses) { expenses.filter { it.status == "Pending" } }
    val totalDue = remember(pendingExpenses) { pendingExpenses.sumOf { it.amount } }
    val totalInvestment = remember(investors) { investors.sumOf { it.totalInvestment } }
    val cashInHand = remember(totalInvestment, totalSpent) { (totalInvestment - totalSpent).coerceAtLeast(0.0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card (Exact style inspired by screenshot)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = t["welcome"] ?: "Welcome!",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "${t["member"] ?: "Member"}: ${activeInvestor?.name ?: "Guest"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                    }
                    Button(
                        onClick = onSwitchInvestor,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftMint,
                            contentColor = DeepGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = t["change_member"],
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = t["change_member"] ?: "Change Profile",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Room Sync Notification Card (Exact match of warning/sync card in screenshot)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MintGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(DeepGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Synced",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = t["sync_active"] ?: "Automatic Offline Sync Active (Room)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = t["sync_desc"] ?: "All data is securely persisted in SQLite offline-first.",
                            fontSize = 12.sp,
                            color = ForestGreen.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Net Profit Hero Card (Deep Green Vibe inspired by the screenshot)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t["net_profit"] ?: "Net Profit",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "জুন ২০২৬" else "June 2026",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "৳ ${formatCurrency(netProfit)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = t["total_budget"] ?: "Total Budget",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "৳ ${formatCurrency(totalBudget)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                                .align(Alignment.CenterVertically)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = t["total_spend"] ?: "Total Spent",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "৳ ${formatCurrency(totalSpent)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Financial Metrics 2-Column Grid with rounded-2xl modern flat design
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricGridItem(
                        title = t["total_loss"] ?: "Total Loss",
                        value = "৳ ${formatCurrency(totalLoss)}",
                        icon = Icons.Default.TrendingDown,
                        iconBgColor = Color(0xFFFEE2E2),
                        iconColor = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                    MetricGridItem(
                        title = t["total_due"] ?: "Total Due",
                        value = "৳ ${formatCurrency(totalDue)}",
                        icon = Icons.Default.HourglassEmpty,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
                MetricGridItem(
                    title = t["cash_in_hand"] ?: "Cash in Hand",
                    value = "৳ ${formatCurrency(cashInHand)}",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconBgColor = Color(0xFFD1FAE5),
                    iconColor = DeepGreen,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Comparison Chart & Ratio View
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t["compare_title"] ?: "Income & Expense Comparison",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    val spentRatio = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f
                    val displayPercentage = (spentRatio * 100).coerceIn(0f, 100f)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        Canvas(modifier = Modifier.size(140.dp)) {
                            // Circular Progress Draw
                            drawArc(
                                color = MintGreen,
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (spentRatio > 0.8f) AlertRed else DeepGreen,
                                startAngle = 135f,
                                sweepAngle = 270f * spentRatio.coerceIn(0f, 1f),
                                useCenter = false,
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = t["spent_ratio"] ?: "Spent Ratio",
                                fontSize = 12.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format("%.1f%%", displayPercentage),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }

        // Projects Section
        item {
            Text(
                text = t["projects_list"] ?: "Active Projects Tracker",
                fontWeight = FontWeight.Bold,
                color = DeepGreen,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (projects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No active projects found. Click '+' to start.", color = TextMuted)
                }
            }
        } else {
            items(projects) { project ->
                ProjectCard(
                    project = project,
                    t = t,
                    isAdmin = (activeInvestor?.role == "Super Admin"),
                    onDelete = onDeleteProject
                )
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, t: Map<String, String>, isAdmin: Boolean, onDelete: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )
                    Text(
                        text = project.description,
                        fontSize = 12.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                val statusBg = when (project.status) {
                    "Active" -> MintGreen
                    "Proposed" -> SoftMint
                    else -> LightBg
                }
                val statusText = when (project.status) {
                    "Active" -> DeepGreen
                    "Proposed" -> AlertGold
                    else -> TextMuted
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = project.status,
                            fontSize = 11.sp,
                            color = statusText,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isAdmin) {
                        var showConfirmDelete by remember { mutableStateOf(false) }

                        if (showConfirmDelete) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDelete = false },
                                title = { Text(text = t["delete_project"] ?: "Delete Project", color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                                text = { Text(text = t["delete_confirm"] ?: "Are you sure you want to delete this project?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            onDelete(project.id)
                                            showConfirmDelete = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                                    ) {
                                        Text("Delete", color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showConfirmDelete = false }) {
                                        Text("Cancel", color = TextMuted)
                                    }
                                }
                            )
                        }

                        IconButton(
                            onClick = { showConfirmDelete = true },
                            modifier = Modifier.size(28.dp).testTag("btn_delete_project_${project.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Project",
                                tint = AlertRed.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val progress = if (project.budget > 0) (project.currentSpend / project.budget).toFloat() else 0f
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress > 0.9f) AlertRed else AccentGreen,
                trackColor = MintGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${t["spent_label"]}: ৳ ${formatCurrency(project.currentSpend)}",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${t["budget_label"]}: ৳ ${formatCurrency(project.budget)}",
                    fontSize = 12.sp,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InvestorsScreen(investors: List<Investor>, viewModel: TrackerViewModel, t: Map<String, String>) {
    var showAddInvestorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = t["investor_profiles"] ?: "Investor Profiles",
                fontWeight = FontWeight.Bold,
                color = DeepGreen,
                fontSize = 20.sp
            )
            Button(
                onClick = { showAddInvestorDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = t["add_investor"],
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = t["add_investor"] ?: "Add Profile", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (investors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No investors registered.", color = TextMuted)
            }
        } else {
            val avatarGradients = listOf(
                Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))), // Blue
                Brush.linearGradient(colors = listOf(Color(0xFF10B981), Color(0xFF047857))), // Green
                Brush.linearGradient(colors = listOf(Color(0xFFEC4899), Color(0xFFBE185D))), // Pink
                Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))), // Purple
                Brush.linearGradient(colors = listOf(Color(0xFFF59E0B), Color(0xFFB45309))), // Amber
                Brush.linearGradient(colors = listOf(Color(0xFFEF4444), Color(0xFFB91C1C)))  // Red
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(investors.indices.toList()) { index ->
                    val investor = investors[index]
                    val brush = avatarGradients[index % avatarGradients.size]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(brush),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = investor.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = investor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextDark
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PieChart,
                                            contentDescription = null,
                                            tint = AccentGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${t["share"]}: ${investor.sharePercentage}%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentGreen
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = SoftMint)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingUp, null, tint = DeepGreen, modifier = Modifier.size(14.dp))
                                        Text(text = t["total_invested"] ?: "Total Invested", fontSize = 11.sp, color = TextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "৳ ${formatCurrency(investor.totalInvestment)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextDark
                                    )
                                }
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingDown, null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                        Text(text = t["withdrawals"] ?: "Withdrawals", fontSize = 11.sp, color = TextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "৳ ${formatCurrency(investor.withdrawals)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = SoftMint.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Phone, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    Text(text = investor.phone, fontSize = 12.sp, color = TextDark)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Email, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    Text(text = investor.email, fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddInvestorDialog) {
        Dialog(onDismissRequest = { showAddInvestorDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var capital by remember { mutableStateOf("") }
                var share by remember { mutableStateOf("") }
                var roleSelection by remember { mutableStateOf("General Investor") }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = t["add_investor"] ?: "Add Investor Profile",
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = capital,
                        onValueChange = { capital = it },
                        label = { Text("Investment Amount (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = share,
                        onValueChange = { share = it },
                        label = { Text("Equity Share (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Authority Role:", fontSize = 12.sp, color = TextMuted)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Super Admin", "Witness", "General Investor").forEach { roleName ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (roleSelection == roleName) SoftMint else LightBg)
                                    .border(1.dp, if (roleSelection == roleName) DeepGreen else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { roleSelection = roleName }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = roleName.replace("General ", ""),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (roleSelection == roleName) ForestGreen else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddInvestorDialog = false }) {
                            Text(text = t["cancel"] ?: "Cancel", color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val cVal = capital.toDoubleOrNull() ?: 0.0
                                val sVal = share.toDoubleOrNull() ?: 0.0
                                if (name.isNotBlank()) {
                                    viewModel.createInvestor(name, email, phone, cVal, sVal, roleSelection)
                                    showAddInvestorDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
                        ) {
                            Text(text = t["submit"] ?: "Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovalsScreen(
    pendingExpenses: List<Expense>,
    activeInvestor: Investor?,
    onVote: (Expense, String, String, Boolean) -> Unit,
    t: Map<String, String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = t["pending_reviews"] ?: "Approvals Review Queue",
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (pendingExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = "Empty approvals",
                        tint = AccentGreen,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All caught up!",
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "No expenses are pending review.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(pendingExpenses) { expense ->
                    val approvesList = expense.witnessApprovals.split(",").filter { it.isNotBlank() }
                    val rejectionsList = expense.witnessRejections.split(",").filter { it.isNotBlank() }
                    val voteCount = approvesList.size
                    val hasVotedApprove = approvesList.contains(activeInvestor?.name)
                    val hasVotedReject = rejectionsList.contains(activeInvestor?.name)

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LightSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = expense.category,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextDark
                                    )
                                    Text(
                                        text = expense.projectName,
                                        fontSize = 13.sp,
                                        color = AccentGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "৳ ${formatCurrency(expense.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = AlertRed
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = expense.description,
                                fontSize = 12.sp,
                                color = TextDark.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Receipt Image Section
                            Text(
                                text = "Receipt / Voucher Copy:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.img_receipt_sample),
                                contentDescription = "Receipt Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, SoftMint, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Submitter Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${t["submitted_by"]}: ${expense.submittedBy}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = SoftMint.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Witness Vote Progress Tracking
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t["witness_progress"] ?: "Witness Signatures:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "$voteCount/3 Approved",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (voteCount >= 3) ForestGreen else AlertGold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            LinearProgressIndicator(
                                progress = (voteCount / 3f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (voteCount >= 3) ForestGreen else AccentGreen,
                                trackColor = MintGreen
                            )

                            if (approvesList.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Approvers: " + approvesList.joinToString(", "),
                                    fontSize = 11.sp,
                                    color = ForestGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (rejectionsList.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rejections: " + rejectionsList.joinToString(", "),
                                    fontSize = 11.sp,
                                    color = AlertRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Voting Controls
                            val userRole = activeInvestor?.role ?: "General Investor"
                            if (userRole == "Super Admin") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { onVote(expense, activeInvestor?.name ?: "Admin", userRole, false) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AlertRedBg,
                                            contentColor = AlertRed
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(text = "Admin Reject", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onVote(expense, activeInvestor?.name ?: "Admin", userRole, true) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DeepGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = "Admin Approve", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            } else if (userRole == "Witness") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { onVote(expense, activeInvestor?.name ?: "Witness", userRole, false) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (hasVotedReject) AlertRed else TextMuted
                                        ),
                                        border = BorderStroke(1.dp, if (hasVotedReject) AlertRed else SoftMint),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(text = if (hasVotedReject) "Voted Reject" else "Vote Reject", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { onVote(expense, activeInvestor?.name ?: "Witness", userRole, true) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (hasVotedApprove) ForestGreen else DeepGreen
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(text = if (hasVotedApprove) "Voted Approve" else "Vote Approve", fontSize = 11.sp)
                                    }
                                }
                            } else {
                                // Guest or General Investor view
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LightBg)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Read-Only: Witness/Admin vote required for approval",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    projects: List<Project>,
    expenses: List<Expense>,
    investors: List<Investor>,
    activeInvestor: Investor?,
    onUpdateInvestor: (Investor) -> Unit,
    onLogout: () -> Unit,
    isBengali: Boolean,
    onToggleBengali: (Boolean) -> Unit,
    onReset: () -> Unit,
    t: Map<String, String>
) {
    var updatePhone by remember { mutableStateOf(activeInvestor?.phone ?: "") }
    var updatePassword by remember { mutableStateOf(activeInvestor?.password ?: "") }
    var successMsg by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    // Keep state updated if activeInvestor changes
    LaunchedEffect(activeInvestor) {
        if (activeInvestor != null) {
            updatePhone = activeInvestor.phone
            updatePassword = activeInvestor.password
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = t["settings"] ?: "Settings",
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Database Statistics Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = t["stats"] ?: "Database Stats",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = t["db_projects"] ?: "Total Projects", color = TextMuted)
                    Text(text = "${projects.size}", fontWeight = FontWeight.Bold, color = TextDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = t["db_expenses"] ?: "Expense Records", color = TextMuted)
                    Text(text = "${expenses.size}", fontWeight = FontWeight.Bold, color = TextDark)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = t["db_investors"] ?: "Registered Investors", color = TextMuted)
                    Text(text = "${investors.size}", fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }

        // Credentials / Security Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "নিরাপত্তা ও অ্যাকাউন্ট বিবরণী" else "Account & Credentials Security",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )

                if (activeInvestor != null) {
                    Text(
                        text = (if (isBengali) "ব্যবহারকারী: " else "Logged in as: ") + activeInvestor.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Text(
                        text = (if (isBengali) "রোল / পদবি: " else "Authority Role: ") + activeInvestor.role,
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Phone textfield (Enabled only for Super Admin to change post-login)
                    val isSuperAdmin = activeInvestor.role == "Super Admin"
                    OutlinedTextField(
                        value = updatePhone,
                        onValueChange = { 
                            updatePhone = it
                            successMsg = ""
                            errorMsg = ""
                        },
                        label = { Text(if (isBengali) "লগইন ফোন নম্বর" else "Login Phone Number") },
                        enabled = isSuperAdmin,
                        supportingText = {
                            if (!isSuperAdmin) {
                                Text(
                                    text = if (isBengali) "শুধুমাত্র সুপার এডমিন ফোন নম্বর পরিবর্তন করতে পারবেন" else "Only Super Admin can change login phone number",
                                    fontSize = 10.sp
                                )
                            }
                        },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password textfield (All users can change post-login)
                    OutlinedTextField(
                        value = updatePassword,
                        onValueChange = { 
                            updatePassword = it
                            successMsg = ""
                            errorMsg = ""
                        },
                        label = { Text(if (isBengali) "লগইন পাসওয়ার্ড" else "Login Password") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (successMsg.isNotBlank()) {
                        Text(text = successMsg, color = ForestGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (errorMsg.isNotBlank()) {
                        Text(text = errorMsg, color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (updatePassword.trim().isBlank() || updatePhone.trim().isBlank()) {
                                errorMsg = if (isBengali) "ফোন এবং পাসওয়ার্ড খালি রাখা যাবে না।" else "Phone and Password cannot be empty."
                                return@Button
                            }
                            val updated = activeInvestor.copy(
                                phone = updatePhone.trim(),
                                password = updatePassword.trim()
                            )
                            onUpdateInvestor(updated)
                            successMsg = if (isBengali) "ক্রিডেনশিয়াল সফলভাবে আপডেট করা হয়েছে!" else "Credentials updated successfully!"
                            errorMsg = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "ক্রিডেনশিয়াল আপডেট করুন" else "Update Credentials",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(text = if (isBengali) "কোনো প্রোফাইল লগইন করা নেই" else "No active user logged in")
                }
            }
        }

        // Language Switch Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = t["language"] ?: "Language",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                    Text(
                        text = if (isBengali) "বাংলা সংস্করণ সক্রিয়" else "English is active",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = isBengali,
                    onCheckedChange = onToggleBengali,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LightSurface,
                        checkedTrackColor = DeepGreen,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SoftMint
                    )
                )
            }
        }

        // Account Logout Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftMint, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBengali) "অ্যাকাউন্ট অ্যাকশন" else "Account Session",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isBengali) "লগআউট করুন" else "Log Out Securely", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Reset Database Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F1)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFFFCCCC), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "System Actions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Use this tool to clear the SQLite database and seed initial demo projects.",
                    fontSize = 12.sp,
                    color = Color(0xFFD32F2F).copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = t["reset_db"] ?: "Reset Database", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectExpenseBottomSheetContent(
    projects: List<Project>,
    activeUser: String,
    onSubmitProject: (String, String, Double, String) -> Unit,
    onSubmitExpense: (Int, String, Double, String, String, String) -> Unit,
    onDismiss: () -> Unit,
    t: Map<String, String>
) {
    Card(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SoftMint, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
    ) {
        var selectedTab by remember { mutableStateOf(0) } // 0 = Project, 1 = Expense
        var uploadedImageName by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier.padding(20.dp)) {
            // Drag handle at top
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.6f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = t["add_title"] ?: "Create New Entry",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepGreen,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = LightBg,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp),
                indicator = @Composable { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DeepGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0
                        uploadedImageName = null
                    },
                    text = { Text(text = t["add_project"] ?: "Project", fontWeight = FontWeight.Bold) },
                    selectedContentColor = DeepGreen,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        uploadedImageName = null
                    },
                    text = { Text(text = t["add_expense"] ?: "Expense", fontWeight = FontWeight.Bold) },
                    selectedContentColor = DeepGreen,
                    unselectedContentColor = TextMuted
                )
            }

            if (selectedTab == 0) {
                // Project Form
                var name by remember { mutableStateOf("") }
                var desc by remember { mutableStateOf("") }
                var budget by remember { mutableStateOf("") }
                var date by remember { mutableStateOf("") }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(t["project_name"] ?: "Project Name") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_project_name")
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text(t["description"] ?: "Description") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_project_desc")
                    )
                    OutlinedTextField(
                        value = budget,
                        onValueChange = { budget = it },
                        label = { Text(t["budget_input"] ?: "Funding Budget (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_project_budget")
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(t["start_date"] ?: "Start Date (YYYY-MM-DD)") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_project_date")
                    )

                    // Image Upload Field
                    Text(
                        text = t["image_upload"] ?: "Upload Image (Optional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightBg)
                            .border(1.dp, if (uploadedImageName != null) AccentGreen else SoftMint, RoundedCornerShape(12.dp))
                            .clickable {
                                uploadedImageName = "project_hero_" + (1000..9999).random().toString() + ".jpg"
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (uploadedImageName != null) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                            contentDescription = "Upload Photo",
                            tint = if (uploadedImageName != null) AccentGreen else DeepGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uploadedImageName ?: (t["image_upload"] ?: "Upload Image (Optional)"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uploadedImageName != null) AccentGreen else TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = t["cancel"] ?: "Cancel", color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val bVal = budget.toDoubleOrNull() ?: 0.0
                                if (name.isNotBlank() && bVal > 0) {
                                    val formattedDate = date.ifBlank { "2026-06-27" }
                                    onSubmitProject(name, desc, bVal, formattedDate)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                            modifier = Modifier.testTag("submit_project_btn")
                        ) {
                            Text(text = t["submit"] ?: "Submit")
                        }
                    }
                }
            } else {
                // Expense Form
                var projectId by remember { mutableStateOf(-1) }
                var projectName by remember { mutableStateOf("") }
                var amount by remember { mutableStateOf("") }
                var category by remember { mutableStateOf("Equipment") }
                var desc by remember { mutableStateOf("") }
                var expandedProjectDropdown by remember { mutableStateOf(false) }
                var expandedCategoryDropdown by remember { mutableStateOf(false) }

                val categories = listOf("Seeds & Nutrients", "Equipment", "Labor", "Utility", "Machinery", "Other")

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Project Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = projectName.ifBlank { t["select_project"] ?: "Select Project" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(t["select_project"] ?: "Project") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            colors = defaultTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedProjectDropdown = true }
                                .testTag("dropdown_project")
                        )
                        DropdownMenu(
                            expanded = expandedProjectDropdown,
                            onDismissRequest = { expandedProjectDropdown = false }
                        ) {
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj.name) },
                                    onClick = {
                                        projectId = proj.id
                                        projectName = proj.name
                                        expandedProjectDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Category Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(t["category"] ?: "Category") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            colors = defaultTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedCategoryDropdown = true }
                                .testTag("dropdown_category")
                        )
                        DropdownMenu(
                            expanded = expandedCategoryDropdown,
                            onDismissRequest = { expandedCategoryDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expandedCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(t["amount_input"] ?: "Amount (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_expense_amount")
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text(t["description"] ?: "Description") },
                        colors = defaultTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().testTag("input_expense_desc")
                    )

                    // Image Upload Field for Receipt/Voucher
                    Text(
                        text = t["image_upload"] ?: "Upload Image (Optional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightBg)
                            .border(1.dp, if (uploadedImageName != null) AccentGreen else SoftMint, RoundedCornerShape(12.dp))
                            .clickable {
                                uploadedImageName = "receipt_" + (1000..9999).random().toString() + ".jpg"
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (uploadedImageName != null) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                            contentDescription = "Upload Receipt",
                            tint = if (uploadedImageName != null) AccentGreen else DeepGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uploadedImageName ?: (t["image_upload"] ?: "Upload Image (Optional)"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uploadedImageName != null) AccentGreen else TextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = t["cancel"] ?: "Cancel", color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val aVal = amount.toDoubleOrNull() ?: 0.0
                                if (projectId != -1 && aVal > 0) {
                                    onSubmitExpense(projectId, projectName, aVal, category, desc, activeUser)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                            modifier = Modifier.testTag("submit_expense_btn")
                        ) {
                            Text(text = t["submit"] ?: "Submit")
                        }
                    }
                }
            }
        }
    }
}

// Helper to format currency values beautifully
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(amount)
}

@Composable
fun ChatScreen(
    chatMessages: List<com.example.data.model.ChatMessage>,
    activeInvestor: Investor?,
    isOnline: Boolean,
    onSendMessage: (String, String) -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    t: Map<String, String>
) {
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header with Online/Offline Switch
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isOnline) SoftMint else AlertRedBg.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isOnline) "🟢 ${t["online"] ?: "Online"}" else "🔴 ${t["offline"] ?: "Offline"}",
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) ForestGreen else AlertRed,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isOnline) (t["online_desc"] ?: "Real-time updates active") else (t["offline_mode"] ?: "Offline Mode"),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Button(
                    onClick = { onToggleOnline(!isOnline) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isOnline) ForestGreen else AlertRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isOnline) "Go Offline" else "Sync & Go Online",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Chat messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChatBubbleOutline, null, tint = AccentGreen, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No messages yet. Start the conversation!", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(chatMessages) { msg ->
                    val isOwnMessage = msg.sender == (activeInvestor?.name ?: "Guest")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isOwnMessage) 12.dp else 0.dp,
                                bottomEnd = if (isOwnMessage) 0.dp else 12.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isOwnMessage) DeepGreen else LightSurface
                            ),
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .border(
                                    1.dp,
                                    if (isOwnMessage) Color.Transparent else SoftMint,
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isOwnMessage) 12.dp else 0.dp,
                                        bottomEnd = if (isOwnMessage) 0.dp else 12.dp
                                    )
                                )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!isOwnMessage) {
                                    Text(
                                        text = msg.sender,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = msg.message,
                                    fontSize = 13.sp,
                                    color = if (isOwnMessage) Color.White else TextDark
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 9.sp,
                                        color = if (isOwnMessage) Color.White.copy(alpha = 0.7f) else TextMuted
                                    )
                                    if (!msg.isSynced) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Pending Sync",
                                            tint = if (isOwnMessage) Color.White.copy(alpha = 0.7f) else AlertGold,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type message...") },
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text")
            )
            FloatingActionButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(activeInvestor?.name ?: "Guest", messageText)
                        messageText = ""
                    }
                },
                containerColor = DeepGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(Icons.Default.Send, "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun HistoryLogsDialog(
    historyLogs: List<com.example.data.model.HistoryLog>,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(8.dp)
                .border(1.dp, SoftMint, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History Log Icon",
                            tint = DeepGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isBengali) "লেনদেন ও সিস্টেম ইতিহাস" else "Transaction & System History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepGreen
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Divider(color = SoftMint, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                if (historyLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = TextMuted.copy(alpha = 0.5f)
                            )
                            Text(
                                text = if (isBengali) "কোনো ইতিহাস রেকর্ড পাওয়া যায়নি।" else "No history logs found.",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(historyLogs.sortedByDescending { it.timestamp }) { log ->
                            // Custom color coding depending on Action Type
                            val badgeColor = when (log.actionType) {
                                "Investment" -> ForestGreen
                                "Project Added" -> ForestGreen
                                "Expense Submission" -> Color(0xFFE65100) // Dark orange
                                "Expense Approval" -> Color(0xFF006064) // Cyan
                                "Expense Rejection" -> AlertRed
                                "Project Deleted" -> AlertRed
                                else -> DeepGreen
                            }

                            val badgeBg = when (log.actionType) {
                                "Investment" -> SoftMint
                                "Project Added" -> SoftMint
                                "Expense Submission" -> Color(0xFFFFF3E0)
                                "Expense Approval" -> Color(0xFFE0F7FA)
                                "Expense Rejection" -> Color(0xFFFFEBEE)
                                "Project Deleted" -> Color(0xFFFFEBEE)
                                else -> SoftMint
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = LightBg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SoftMint.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Action Type Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(badgeBg)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isBengali) {
                                                    when (log.actionType) {
                                                        "Investment" -> "বিনিয়োগ"
                                                        "Project Added" -> "প্রজেক্ট যোগ"
                                                        "Expense Submission" -> "ব্যয় জমা"
                                                        "Expense Approval" -> "ব্যয় অনুমোদন"
                                                        "Expense Rejection" -> "ব্যয় বাতিল"
                                                        "Project Deleted" -> "প্রজেক্ট ডিলিট"
                                                        else -> log.actionType
                                                    }
                                                } else log.actionType,
                                                color = badgeColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Formatted Date
                                        val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                                            .format(Date(log.timestamp))
                                        Text(
                                            text = dateStr,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Log Description text
                                    Text(
                                        text = log.description,
                                        fontSize = 13.sp,
                                        color = TextDark,
                                        fontWeight = FontWeight.Normal
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Member indicator
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = ForestGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = log.memberName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreen
                                            )
                                        }

                                        // Amount Indicator if any
                                        if (log.amount > 0) {
                                            Text(
                                                text = "৳" + NumberFormat.getNumberInstance(Locale.US).format(log.amount),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = badgeColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isBengali) "বন্ধ করুন" else "Close History", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

