package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinGuideRepository(context: Context) {
  private val db = AppDatabase.getDatabase(context)
  private val userDao = db.userDao()
  private val transactionDao = db.transactionDao()
  private val budgetDao = db.budgetDao()
  private val savingsDao = db.savingsDao()
  private val resourceDao = db.resourceDao()
  private val documentDao = db.documentDao()
  private val articleDao = db.articleDao()
  private val notificationDao = db.notificationDao()
  private val reportDao = db.reportDao()
  private val chatDao = db.chatDao()

  init {
    CoroutineScope(Dispatchers.IO).launch {
      seedInitialDataIfNeeded()
    }
  }

  // --- Auth & Users ---
  suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
  suspend fun getUserById(id: String) = userDao.getUserById(id)
  suspend fun registerUser(user: User) = userDao.insertUser(user)
  suspend fun updateUser(user: User) = userDao.updateUser(user)
  fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
  suspend fun setUserStatus(userId: String, isActive: Boolean) = userDao.setUserActiveStatus(userId, isActive)

  // --- Transactions ---
  fun getTransactions(userId: String): Flow<List<TransactionEntity>> =
    transactionDao.getTransactionsForUser(userId)

  suspend fun addTransaction(transaction: TransactionEntity) {
    transactionDao.insertTransaction(transaction)
    // Check budget limit alert
    if (transaction.type == "EXPENSE") {
      checkBudgetThreshold(transaction.userId, transaction.category)
    }
  }

  suspend fun updateTransaction(transaction: TransactionEntity) {
    transactionDao.updateTransaction(transaction)
    if (transaction.type == "EXPENSE") {
      checkBudgetThreshold(transaction.userId, transaction.category)
    }
  }

  suspend fun deleteTransaction(id: String, userId: String) =
    transactionDao.deleteTransaction(id, userId)

  // --- Budgets ---
  fun getBudgets(userId: String): Flow<List<BudgetEntity>> =
    budgetDao.getBudgetsForUser(userId)

  suspend fun saveBudget(budget: BudgetEntity) {
    budgetDao.insertBudget(budget)
  }

  suspend fun deleteBudget(id: String, userId: String) =
    budgetDao.deleteBudget(id, userId)

  private suspend fun checkBudgetThreshold(userId: String, category: String) {
    val budget = budgetDao.getBudgetByCategory(userId, category) ?: return
    val transactions = transactionDao.getTransactionsForUser(userId).firstOrNull() ?: emptyList()
    val spent = transactions
      .filter { it.type == "EXPENSE" && it.category.equals(category, ignoreCase = true) }
      .sumOf { it.amount }

    if (spent >= budget.monthlyLimit) {
      notificationDao.insertNotification(
        NotificationEntity(
          id = UUID.randomUUID().toString(),
          userId = userId,
          title = "Budget Exceeded: $category",
          message = "You have exceeded your monthly $category budget of ₹${budget.monthlyLimit.toInt()} (Current spend: ₹${spent.toInt()}).",
          type = "BUDGET_ALERT"
        )
      )
    } else if (spent >= (budget.monthlyLimit * 0.85)) {
      notificationDao.insertNotification(
        NotificationEntity(
          id = UUID.randomUUID().toString(),
          userId = userId,
          title = "Budget Warning: $category",
          message = "You have used ${(spent / budget.monthlyLimit * 100).toInt()}% of your monthly $category budget.",
          type = "BUDGET_ALERT"
        )
      )
    }
  }

  // --- Savings Goals ---
  fun getSavingsGoals(userId: String): Flow<List<SavingsGoalEntity>> =
    savingsDao.getGoalsForUser(userId)

  suspend fun saveSavingsGoal(goal: SavingsGoalEntity) =
    savingsDao.insertGoal(goal)

  suspend fun addSavingsDeposit(goalId: String, depositAmount: Double, userId: String) {
    val goal = savingsDao.getGoalById(goalId) ?: return
    val updated = goal.copy(currentAmount = goal.currentAmount + depositAmount)
    savingsDao.updateGoal(updated)
    
    // Check if goal completed
    if (updated.currentAmount >= updated.targetAmount) {
      notificationDao.insertNotification(
        NotificationEntity(
          id = UUID.randomUUID().toString(),
          userId = userId,
          title = "Goal Achieved: ${goal.title}",
          message = "Congratulations! You have reached your savings target of ₹${goal.targetAmount.toInt()} for ${goal.title}!",
          type = "SAVINGS_REMINDER"
        )
      )
    }
  }

  suspend fun deleteSavingsGoal(id: String, userId: String) =
    savingsDao.deleteGoal(id, userId)

  // --- Verified Resources ---
  fun getAllActiveResources(): Flow<List<ResourceEntity>> =
    resourceDao.getAllActiveResources()

  fun getAllResourcesAdmin(): Flow<List<ResourceEntity>> =
    resourceDao.getAllResourcesAdmin()

  suspend fun saveResource(resource: ResourceEntity) =
    resourceDao.insertResource(resource)

  suspend fun updateResourceVerification(id: String, status: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    resourceDao.updateVerification(id, status, sdf.format(Date()))
  }

  suspend fun deleteResource(id: String) =
    resourceDao.deleteResource(id)

  suspend fun submitResourceReport(report: ResourceReportEntity) =
    reportDao.insertReport(report)

  fun getAllReports(): Flow<List<ResourceReportEntity>> =
    reportDao.getAllReports()

  suspend fun updateReport(id: String, status: String, adminNote: String) =
    reportDao.updateReportStatus(id, status, adminNote)

  // --- Documents ---
  fun getDocuments(userId: String): Flow<List<DocumentEntity>> =
    documentDao.getDocumentsForUser(userId)

  suspend fun saveDocument(doc: DocumentEntity) =
    documentDao.insertDocument(doc)

  suspend fun deleteDocument(id: String, userId: String) =
    documentDao.deleteDocument(id, userId)

  // --- Educational Articles ---
  fun getPublishedArticles(): Flow<List<ArticleEntity>> =
    articleDao.getPublishedArticles()

  fun getAllArticlesAdmin(): Flow<List<ArticleEntity>> =
    articleDao.getAllArticlesAdmin()

  suspend fun saveArticle(article: ArticleEntity) =
    articleDao.insertArticle(article)

  suspend fun deleteArticle(id: String) =
    articleDao.deleteArticle(id)

  // --- Notifications ---
  fun getNotifications(userId: String): Flow<List<NotificationEntity>> =
    notificationDao.getNotificationsForUser(userId)

  suspend fun markNotificationRead(id: String) =
    notificationDao.markAsRead(id)

  suspend fun markAllNotificationsRead(userId: String) =
    notificationDao.markAllAsRead(userId)

  suspend fun sendBroadcastNotification(title: String, message: String, type: String = "SYSTEM") {
    notificationDao.insertNotification(
      NotificationEntity(
        id = UUID.randomUUID().toString(),
        userId = "ALL",
        title = title,
        message = message,
        type = type
      )
    )
  }

  // --- Chat ---
  fun getChatHistory(userId: String): Flow<List<ChatMessageEntity>> =
    chatDao.getChatHistory(userId)

  suspend fun saveChatMessage(message: ChatMessageEntity) =
    chatDao.insertMessage(message)

  // --- Seeder ---
  private suspend fun seedInitialDataIfNeeded() {
    // 1. Seed Users if empty
    val existingAdmin = userDao.getUserByEmail("admin@finguide.com")
    if (existingAdmin == null) {
      userDao.insertUser(
        User(
          id = "admin-1",
          name = "FinGuide Administrator",
          email = "admin@finguide.com",
          passwordHash = "admin123",
          address = "Admin Secretariat Complex, Rajaji Salai",
          city = "Chennai",
          state = "Tamil Nadu",
          postalCode = "600009",
          role = "ADMIN",
          userType = "WORKING_PROFESSIONAL",
          preferredLanguage = "en"
        )
      )
    }

    val existingUser = userDao.getUserByEmail("student@finguide.com")
    val defaultUserId = "user-demo-1"
    if (existingUser == null) {
      userDao.insertUser(
        User(
          id = defaultUserId,
          name = "Aarav Sharma",
          email = "student@finguide.com",
          passwordHash = "demo123",
          address = "12/4 Anna Salai, T. Nagar",
          city = "Chennai",
          state = "Tamil Nadu",
          postalCode = "600017",
          role = "USER",
          userType = "STUDENT",
          preferredLanguage = "en"
        )
      )

      // Seed initial transactions
      val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val today = sdf.format(Date())
      val cal = Calendar.getInstance()
      cal.add(Calendar.DAY_OF_YEAR, -2)
      val twoDaysAgo = sdf.format(cal.time)
      cal.add(Calendar.DAY_OF_YEAR, -5)
      val sevenDaysAgo = sdf.format(cal.time)

      transactionDao.insertTransaction(
        TransactionEntity("t1", defaultUserId, "INCOME", 15000.0, "Salary", "Part-time Internship Stipend", sevenDaysAgo, "Bank Transfer")
      )
      transactionDao.insertTransaction(
        TransactionEntity("t2", defaultUserId, "INCOME", 2500.0, "Freelance", "Graphic Design Project", twoDaysAgo, "UPI")
      )
      transactionDao.insertTransaction(
        TransactionEntity("t3", defaultUserId, "EXPENSE", 450.0, "Food", "College Canteen Meals & Snacks", today, "UPI")
      )
      transactionDao.insertTransaction(
        TransactionEntity("t4", defaultUserId, "EXPENSE", 180.0, "Transport", "Metro SmartCard Recharge", twoDaysAgo, "UPI")
      )
      transactionDao.insertTransaction(
        TransactionEntity("t5", defaultUserId, "EXPENSE", 1200.0, "Education", "Semester Textbook & Stationery", sevenDaysAgo, "Card")
      )
      transactionDao.insertTransaction(
        TransactionEntity("t6", defaultUserId, "EXPENSE", 650.0, "Bills", "Mobile Postpaid & Data Plan", sevenDaysAgo, "UPI")
      )

      // Seed Budgets
      budgetDao.insertBudget(BudgetEntity("b1", defaultUserId, "Food", 3500.0, "2026-09"))
      budgetDao.insertBudget(BudgetEntity("b2", defaultUserId, "Transport", 1200.0, "2026-09"))
      budgetDao.insertBudget(BudgetEntity("b3", defaultUserId, "Shopping", 2000.0, "2026-09"))
      budgetDao.insertBudget(BudgetEntity("b4", defaultUserId, "Education", 2500.0, "2026-09"))
      budgetDao.insertBudget(BudgetEntity("b5", defaultUserId, "Bills", 1500.0, "2026-09"))

      // Seed Savings Goals
      savingsDao.insertGoal(
        SavingsGoalEntity("g1", defaultUserId, "Emergency Contingency Fund", 30000.0, 12500.0, "2026-12-31", "Emergency")
      )
      savingsDao.insertGoal(
        SavingsGoalEntity("g2", defaultUserId, "Coding Laptop Upgrade", 55000.0, 22000.0, "2027-03-31", "Electronics")
      )

      // Seed Notifications
      notificationDao.insertNotification(
        NotificationEntity("n1", defaultUserId, "Welcome to FinGuide", "Explore verified financial authorities and manage your monthly money smartly!", "SYSTEM")
      )
      notificationDao.insertNotification(
        NotificationEntity("n2", defaultUserId, "Savings Milestone", "You are 40% towards your Coding Laptop Upgrade goal. Keep it up!", "SAVINGS_REMINDER")
      )
    }

    // 2. Seed Verified Resources (TNREGINET, SEBI, IBBI, BIS, RBI)
    val resList = listOf(
      ResourceEntity(
        id = "res-1",
        name = "State Bank of India - Main Branch",
        category = "Banks",
        address = "16, Rajaji Salai, George Town",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2522 0011",
        latitude = 13.0882,
        longitude = 80.2916,
        authority = "RBI",
        registrationNumber = "RBI/PSB/001",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://www.rbi.org.in",
        lastVerifiedAt = "2026-09-15"
      ),
      ResourceEntity(
        id = "res-2",
        name = "FinWise Capital Advisory LLP",
        category = "Investment Advisers",
        address = "Level 4, Olympia Tech Park, Guindy",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 4390 1200",
        latitude = 13.0118,
        longitude = 80.2074,
        authority = "SEBI",
        registrationNumber = "INA000012345",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://www.sebi.gov.in/sebiweb/other/OtherAction.do?doRecognisedFpi=yes&intmId=13",
        lastVerifiedAt = "2026-09-10"
      ),
      ResourceEntity(
        id = "res-3",
        name = "Apex Asset Registered Valuers",
        category = "Registered Valuers",
        address = "No. 45, Gandhi Irwin Road, Egmore",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2819 4433",
        latitude = 13.0784,
        longitude = 80.2608,
        authority = "IBBI",
        registrationNumber = "IBBI/RV/06/2021/1042",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://ibbi.gov.in/en/service-provider/registered-valuers",
        lastVerifiedAt = "2026-09-08"
      ),
      ResourceEntity(
        id = "res-4",
        name = "Tanishq Hallmarked Jewellery Boutique",
        category = "Jewellers",
        address = "Usman Road, T. Nagar",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2434 5678",
        latitude = 13.0418,
        longitude = 80.2341,
        authority = "BIS",
        registrationNumber = "BIS/HM/TN/2022/990",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://www.manakonline.in",
        lastVerifiedAt = "2026-09-12"
      ),
      ResourceEntity(
        id = "res-5",
        name = "Sub-Registrar Office, T. Nagar",
        category = "Land Registration Offices",
        address = "South Boag Road, CIT Nagar",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2435 1122",
        latitude = 13.0336,
        longitude = 80.2289,
        authority = "TNREGINET",
        registrationNumber = "TN/REG/CHE/04",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://tnreginet.gov.in",
        lastVerifiedAt = "2026-09-18"
      ),
      ResourceEntity(
        id = "res-6",
        name = "Life Insurance Corporation (LIC) Divisional Office",
        category = "Insurance",
        address = "102, Anna Salai",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2852 1431",
        latitude = 13.0645,
        longitude = 80.2678,
        authority = "IRDAI",
        registrationNumber = "IRDAI/LIFE/001",
        verificationStatus = "VERIFIED",
        sourceUrl = "https://irdai.gov.in",
        lastVerifiedAt = "2026-09-14"
      ),
      ResourceEntity(
        id = "res-7",
        name = "Metro Property Title & Verification Services",
        category = "Property Services",
        address = "12, 1st Cross Street, Nungambakkam",
        city = "Chennai",
        state = "Tamil Nadu",
        phone = "+91 44 2827 9000",
        latitude = 13.0604,
        longitude = 80.2407,
        authority = "TNREGINET",
        registrationNumber = "TN/PV/2024/08",
        verificationStatus = "NEEDS_RECHECK",
        sourceUrl = "https://tnreginet.gov.in",
        lastVerifiedAt = "2026-06-20"
      )
    )
    resourceDao.insertAll(resList)

    // 3. Seed Educational Articles across 14 categories
    val articleList = listOf(
      ArticleEntity(
        id = "art-1",
        title = "The 50/30/20 Budgeting Rule for Beginners",
        category = "Budgeting",
        summary = "How to divide your take-home pay into needs, wants, and savings without stress.",
        explanation = "The 50/30/20 rule divides your post-tax income into three buckets: 50% for Essentials (rent, groceries, electricity), 30% for Lifestyle & Wants (dining out, hobbies, subscriptions), and 20% for Financial Goals (emergency fund, debt clearance, investments).",
        example = "If your monthly income is ₹25,000: ₹12,500 for essentials, ₹7,500 for discretionary lifestyle, and ₹5,000 straight into savings.",
        keyPointsJson = "Automate your 20% savings first|Review non-essential subscriptions every quarter|Adjust percentages if rent is above 40%",
        sourceAuthority = "RBI Financial Literacy Guide",
        sourceUrl = "https://rbi.org.in/financialeducation/"
      ),
      ArticleEntity(
        id = "art-2",
        title = "Building Your First 6-Month Emergency Fund",
        category = "Emergency Fund",
        summary = "Why you need liquidity before investing a single rupee in the stock market.",
        explanation = "An emergency fund protects you from unexpected expenses like medical bills, sudden job changes, or urgent repairs. Never compromise liquidity by locking this fund in volatile stocks or lock-in real estate.",
        example = "If your mandatory monthly expenses are ₹15,000, your target emergency fund is ₹90,000 kept in a sweep-in FD or liquid mutual fund.",
        keyPointsJson = "Target 3 to 6 months of mandatory expenses|Keep in high-yield liquid instruments|Do not touch for lifestyle vacations",
        sourceAuthority = "SEBI Investor Awareness",
        sourceUrl = "https://investor.sebi.gov.in/"
      ),
      ArticleEntity(
        id = "art-3",
        title = "Systematic Investment Plans (SIP) Demystified",
        category = "SIP",
        summary = "Harnessing rupee cost averaging and compound interest with small monthly sums.",
        explanation = "A Systematic Investment Plan (SIP) allows an investor to deposit a fixed sum into a mutual fund scheme periodically. It removes the stress of market timing and utilizes Rupee Cost Averaging to buy more units during market dips.",
        example = "Investing ₹2,000 monthly via SIP over 15 years at an estimated 12% CAGR yields approximately ₹10 Lakhs on a total investment of ₹3.6 Lakhs.",
        keyPointsJson = "Rupee-cost averaging reduces volatility risk|Disciplined investing without market timing|Start with as little as ₹500 per month",
        sourceAuthority = "AMFI (Association of Mutual Funds in India)",
        sourceUrl = "https://www.amfiindia.com/"
      ),
      ArticleEntity(
        id = "art-4",
        title = "Understanding Inflation and Purchasing Power",
        category = "Inflation",
        summary = "Why keeping all cash in a zero-interest locker erodes your wealth every year.",
        explanation = "Inflation is the gradual decrease in the purchasing power of money over time. If inflation is 6%, an item costing ₹100 today will cost ₹106 next year. Therefore, your savings return must beat the inflation rate.",
        example = "₹1,00,000 stored in cash loses roughly 40% of its real purchasing power in 10 years at a 5.5% average annual inflation rate.",
        keyPointsJson = "Standard savings accounts (2.5-3%) lose real value against 6% inflation|Invest in growth assets (equity/mutual funds) for long-term beating of inflation|Review interest rates periodically",
        sourceAuthority = "RBI Monetary Policy Department",
        sourceUrl = "https://rbi.org.in/"
      ),
      ArticleEntity(
        id = "art-5",
        title = "Mutual Funds vs Fixed Deposits (FD): Where to Start?",
        category = "Mutual Funds",
        summary = "Comparing risk profiles, guaranteed returns, and wealth accumulation potentials.",
        explanation = "Fixed Deposits provide guaranteed returns with DICGC insurance protection up to ₹5 Lakhs per bank. Mutual funds invest in diversified equities or debt bonds with no guaranteed returns, but offer potential for wealth creation and capital appreciation over 5+ year horizons.",
        example = "Use FDs for short-term goals (< 2 years) like college fees or safety deposits; use Equity Index Mutual Funds for long-term goals (> 5 years).",
        keyPointsJson = "FD returns are taxable as per your income slab|Mutual funds carry market risks and capital gains tax|Match instrument horizon to goal deadline",
        sourceAuthority = "SEBI / RBI Joint Investor Cell",
        sourceUrl = "https://investor.sebi.gov.in/"
      ),
      ArticleEntity(
        id = "art-6",
        title = "Sovereign Gold Bonds (SGB) & BIS Hallmarking",
        category = "Gold",
        summary = "How to invest in gold safely without making charges or locker storage fees.",
        explanation = "Government-backed Sovereign Gold Bonds offer 2.5% annual interest on the initial investment plus capital appreciation linked to gold prices. When buying physical gold, ensure BIS Hallmarking with 6-digit HUID code.",
        example = "Buying 10 grams of SGB gives you market gold appreciation tax-free on maturity, plus ₹1,500+ every year in paid interest.",
        keyPointsJson = "Verify 6-digit HUID via BIS CARE app|Zero making charges and zero GST on sovereign bonds|Tax-free capital gains on 8-year redemption",
        sourceAuthority = "Bureau of Indian Standards (BIS)",
        sourceUrl = "https://www.bis.gov.in/"
      ),
      ArticleEntity(
        id = "art-7",
        title = "Financial Safety & Phishing Protection",
        category = "Financial Safety",
        summary = "Crucial guidelines to prevent UPI scams, fraudulent loan apps, and card skimmers.",
        explanation = "Never share your UPI PIN to receive money. Receiving funds requires zero authentication. Official banks and financial institutions will never ask for your passwords, OTPs, or CVV numbers over WhatsApp or phone calls.",
        example = "A fraudster claims: 'I am sending you ₹500 cashback, enter your UPI PIN to claim.' That is a scam—entering your PIN always debits your account!",
        keyPointsJson = "UPI PIN is only for sending money, never receiving|Never click unverified links claiming instant loans|Report cyber financial fraud immediately to 1930 helpline",
        sourceAuthority = "Indian Cybercrime Coordination Centre (I4C)",
        sourceUrl = "https://cybercrime.gov.in/"
      ),
      ArticleEntity(
        id = "art-8",
        title = "Government Bonds & RBI Retail Direct",
        category = "Government Bonds",
        summary = "Lend directly to the Government of India with zero default risk.",
        explanation = "The RBI Retail Direct scheme allows individual retail investors to directly purchase Treasury Bills (T-Bills) and Government Securities (G-Secs) through an online portal without paying intermediary commissions.",
        example = "Purchase a 91-day T-Bill for safe parking of college fees with sovereign backing.",
        keyPointsJson = "Direct sovereign guarantee by Government of India|Zero account maintenance fees via RBI Retail Direct|Fixed interest coupons paid semi-annually",
        sourceAuthority = "Reserve Bank of India (RBI)",
        sourceUrl = "https://rbiretaildirect.org.in/"
      )
    )
    articleDao.insertAll(articleList)
  }
}
