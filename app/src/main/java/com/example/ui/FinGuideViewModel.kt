package com.example.ui

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.data.model.*
import com.example.data.repository.FinGuideRepository
import com.example.data.sync.FirestoreSyncService
import com.example.data.sync.SyncResult
import com.example.data.sync.SyncState
import com.example.i18n.LanguageManager
import com.example.location.LocationService
import com.example.location.UserCoordinates
import com.example.security.SecurityUtils
import com.example.voice.ParsedExpense
import com.example.voice.VoiceExpenseParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
  SPLASH,
  ONBOARDING,
  LOGIN,
  REGISTER,
  ADMIN_LOGIN,
  DASHBOARD,
  FINANCE,
  ADD_EXPENSE,
  ADD_INCOME,
  VOICE_EXPENSE,
  BUDGET_SAVINGS,
  BUDGETS,
  SAVINGS,
  AI_ASSISTANT,
  AI_CHAT,
  LEARN,
  LEARN_DETAIL,
  RESOURCES,
  RESOURCE_DETAIL,
  DOCUMENTS,
  NOTIFICATIONS,
  PROFILE,
  // Admin Screens
  ADMIN_DASHBOARD,
  ADMIN_USERS,
  ADMIN_RESOURCES,
  ADMIN_VERIFICATION,
  ADMIN_EDUCATION,
  ADMIN_REPORTS,
  ADMIN_NOTIFICATIONS,
  ADMIN_SETTINGS
}

class FinGuideViewModel(application: Application) : AndroidViewModel(application) {
  val repository = FinGuideRepository(application)
  val syncService = FirestoreSyncService(application)

  val syncState: StateFlow<SyncState> = syncService.syncState
  val lastSyncResult: StateFlow<SyncResult?> = syncService.lastSyncResult

  // Navigation & Session
  private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

  private val _currentLanguage = MutableStateFlow("en")
  val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

  private val _forceDesktopLayout = MutableStateFlow<Boolean?>(null) // null = auto detect based on screen width
  val forceDesktopLayout: StateFlow<Boolean?> = _forceDesktopLayout.asStateFlow()

  // Transactions & Budgets
  val transactions: StateFlow<List<TransactionEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getTransactions(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val budgets: StateFlow<List<BudgetEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getBudgets(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val savingsGoals: StateFlow<List<SavingsGoalEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getSavingsGoals(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val documents: StateFlow<List<DocumentEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getDocuments(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getNotifications(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeResources: StateFlow<List<ResourceEntity>> = repository.getAllActiveResources()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val publishedArticles: StateFlow<List<ArticleEntity>> = repository.getPublishedArticles()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Admin Data Flows
  val allUsersAdmin: StateFlow<List<User>> = repository.getAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allResourcesAdmin: StateFlow<List<ResourceEntity>> = repository.getAllResourcesAdmin()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allArticlesAdmin: StateFlow<List<ArticleEntity>> = repository.getAllArticlesAdmin()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allReportsAdmin: StateFlow<List<ResourceReportEntity>> = repository.getAllReports()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Detail Items
  private val _selectedArticle = MutableStateFlow<ArticleEntity?>(null)
  val selectedArticle: StateFlow<ArticleEntity?> = _selectedArticle.asStateFlow()

  private val _selectedResource = MutableStateFlow<ResourceEntity?>(null)
  val selectedResource: StateFlow<ResourceEntity?> = _selectedResource.asStateFlow()

  // Location
  private val _userCoordinates = MutableStateFlow(LocationService.DEFAULT_LOCATION)
  val userCoordinates: StateFlow<UserCoordinates> = _userCoordinates.asStateFlow()

  private val _userMentionedCity = MutableStateFlow("")
  val userMentionedCity: StateFlow<String> = _userMentionedCity.asStateFlow()

  fun setUserMentionedLocation(cityOrAddress: String) {
    _userMentionedCity.value = cityOrAddress.trim()
    if (cityOrAddress.isNotBlank()) {
      val coords = LocationService.geocodeAddress(getApplication(), "", cityOrAddress.trim(), "")
      _userCoordinates.value = coords
    } else {
      refreshLocation()
    }
  }

  // Voice Expense State
  private val _voiceConfirmationCandidate = MutableStateFlow<ParsedExpense?>(null)
  val voiceConfirmationCandidate: StateFlow<ParsedExpense?> = _voiceConfirmationCandidate.asStateFlow()

  private val _isVoiceListening = MutableStateFlow(false)
  val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

  // AI Assistant Chat State
  val chatHistory: StateFlow<List<ChatMessageEntity>> = _currentUser.flatMapLatest { user ->
    if (user != null) repository.getChatHistory(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  init {
    refreshLocation()
  }

  // --- Session & Navigation ---
  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun setLanguage(code: String) {
    _currentLanguage.value = code
    val user = _currentUser.value
    if (user != null) {
      viewModelScope.launch {
        val updated = user.copy(preferredLanguage = code)
        repository.updateUser(updated)
        _currentUser.value = updated
      }
    }
  }

  fun toggleLayoutMode() {
    _forceDesktopLayout.value = when (_forceDesktopLayout.value) {
      null -> true // switch to desktop
      true -> false // switch to mobile
      else -> null // back to auto
    }
  }

  fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val cleanEmail = email.trim().lowercase()
      val user = repository.getUserByEmail(cleanEmail)
      if (user != null && SecurityUtils.verifyPassword(pass, user.passwordHash)) {
        if (!user.isActive) {
          onError("Account is deactivated. Contact administrator.")
          return@launch
        }
        _currentUser.value = user
        _currentLanguage.value = user.preferredLanguage
        if (user.city.isNotBlank() && _userMentionedCity.value.isBlank()) {
          _userMentionedCity.value = user.city
        }
        refreshLocation()
        if (user.role == "ADMIN") {
          _currentScreen.value = AppScreen.ADMIN_DASHBOARD
        } else {
          _currentScreen.value = AppScreen.DASHBOARD
        }
        onSuccess()
      } else {
        onError("Invalid email or password.")
      }
    }
  }

  fun adminLogin(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    viewModelScope.launch {
      val cleanEmail = email.trim().lowercase()
      val user = repository.getUserByEmail(cleanEmail)
      if (user != null && SecurityUtils.verifyPassword(pass, user.passwordHash)) {
        if (!user.isActive) {
          onError("Account is deactivated. Contact system administrator.")
          return@launch
        }
        if (user.role != "ADMIN") {
          onError("Access Denied: Only accounts with ADMIN privileges can enter the administration portal.")
          return@launch
        }
        _currentUser.value = user
        _currentLanguage.value = user.preferredLanguage
        _currentScreen.value = AppScreen.ADMIN_DASHBOARD
        onSuccess()
      } else {
        onError("Invalid administrator credentials. Access restricted.")
      }
    }
  }

  fun register(
    name: String,
    email: String,
    pass: String,
    userType: String,
    lang: String,
    address: String,
    city: String,
    state: String,
    postalCode: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    viewModelScope.launch {
      if (name.isBlank() || email.isBlank() || pass.isBlank()) {
        onError("Please fill in all required fields")
        return@launch
      }
      if (address.isBlank() || city.isBlank()) {
        onError("Address line and city are required")
        return@launch
      }
      val cleanEmail = email.trim().lowercase()
      val existing = repository.getUserByEmail(cleanEmail)
      if (existing != null) {
        onError("User with this email already exists")
        return@launch
      }
      val hashedPassword = SecurityUtils.hashPassword(pass)
      val newUser = User(
        id = UUID.randomUUID().toString(),
        name = name.trim(),
        email = cleanEmail,
        passwordHash = hashedPassword,
        address = address.trim(),
        city = city.trim(),
        state = state.trim(),
        postalCode = postalCode.trim(),
        role = "USER",
        userType = userType,
        preferredLanguage = lang
      )
      repository.registerUser(newUser)
      _currentUser.value = newUser
      _currentLanguage.value = lang
      refreshLocation()
      _currentScreen.value = AppScreen.DASHBOARD
      onSuccess()
    }
  }

  // Menubar & Logout states
  private val _isMenuBarOpen = MutableStateFlow(false)
  val isMenuBarOpen: StateFlow<Boolean> = _isMenuBarOpen.asStateFlow()

  private val _isLoggingOut = MutableStateFlow(false)
  val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

  private val _logoutProgress = MutableStateFlow(0f)
  val logoutProgress: StateFlow<Float> = _logoutProgress.asStateFlow()

  private val _logoutStatusMessage = MutableStateFlow("")
  val logoutStatusMessage: StateFlow<String> = _logoutStatusMessage.asStateFlow()

  private val _showLogoutConfirm = MutableStateFlow(false)
  val showLogoutConfirm: StateFlow<Boolean> = _showLogoutConfirm.asStateFlow()

  fun openMenuBar() {
    _isMenuBarOpen.value = true
  }

  fun closeMenuBar() {
    _isMenuBarOpen.value = false
  }

  fun toggleMenuBar() {
    _isMenuBarOpen.value = !_isMenuBarOpen.value
  }

  fun requestLogout() {
    _showLogoutConfirm.value = true
  }

  fun cancelLogout() {
    _showLogoutConfirm.value = false
  }

  fun executeLogout() {
    _showLogoutConfirm.value = false
    _isMenuBarOpen.value = false
    viewModelScope.launch {
      _isLoggingOut.value = true
      _logoutProgress.value = 0.2f
      _logoutStatusMessage.value = "Securing local ledger..."
      kotlinx.coroutines.delay(350)

      _logoutProgress.value = 0.55f
      _logoutStatusMessage.value = "Encrypting local database..."
      kotlinx.coroutines.delay(400)

      _logoutProgress.value = 0.85f
      _logoutStatusMessage.value = "Clearing active credentials & session cache..."
      kotlinx.coroutines.delay(350)

      _logoutProgress.value = 1.0f
      _logoutStatusMessage.value = "Session securely closed."
      kotlinx.coroutines.delay(250)

      _currentUser.value = null
      _currentScreen.value = AppScreen.LOGIN
      _isLoggingOut.value = false
      _logoutProgress.value = 0f
      _logoutStatusMessage.value = ""
    }
  }

  fun logout() {
    executeLogout()
  }

  fun updateUserProfile(
    name: String,
    email: String,
    userType: String,
    address: String,
    city: String,
    state: String,
    postalCode: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      if (name.isBlank() || email.isBlank()) {
        onError("Name and email cannot be empty")
        return@launch
      }
      if (address.isBlank() || city.isBlank()) {
        onError("Address line and city cannot be empty")
        return@launch
      }
      val updated = user.copy(
        name = name.trim(),
        email = email.trim().lowercase(),
        userType = userType,
        address = address.trim(),
        city = city.trim(),
        state = state.trim(),
        postalCode = postalCode.trim(),
        updatedAt = System.currentTimeMillis()
      )
      repository.updateUser(updated)
      _currentUser.value = updated
      refreshLocation()
      // Automatically trigger offline-first cloud sync to keep Firestore updated
      syncService.migrateRoomToFirestore(updated.id)
      onSuccess()
    }
  }

  fun triggerFirestoreSync(onComplete: (SyncResult) -> Unit = {}) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val result = syncService.migrateRoomToFirestore(user.id)
      onComplete(result)
    }
  }

  fun pullFirestoreToRoom(onComplete: (SyncResult) -> Unit = {}) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val result = syncService.syncFirestoreToRoom(user.id)
      onComplete(result)
    }
  }

  // --- Transactions ---
  fun addExpense(amount: Double, category: String, desc: String, date: String, paymentMode: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val tx = TransactionEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        type = "EXPENSE",
        amount = amount,
        category = category,
        description = desc.ifBlank { "$category expense" },
        date = date,
        paymentMode = paymentMode
      )
      repository.addTransaction(tx)
    }
  }

  fun addIncome(amount: Double, source: String, desc: String, date: String, paymentMode: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val tx = TransactionEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        type = "INCOME",
        amount = amount,
        category = source,
        description = desc.ifBlank { "$source income" },
        date = date,
        paymentMode = paymentMode
      )
      repository.addTransaction(tx)
    }
  }

  fun updateTransaction(tx: TransactionEntity) {
    viewModelScope.launch {
      repository.updateTransaction(tx)
    }
  }

  fun deleteTransaction(txId: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.deleteTransaction(txId, user.id)
    }
  }

  // --- Budgets & Savings ---
  fun saveBudget(category: String, limit: Double) {
    val user = _currentUser.value ?: return
    val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    val monthYear = sdf.format(Date())
    viewModelScope.launch {
      val budget = BudgetEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        category = category,
        monthlyLimit = limit,
        monthYear = monthYear
      )
      repository.saveBudget(budget)
    }
  }

  fun deleteBudget(id: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.deleteBudget(id, user.id)
    }
  }

  fun saveSavingsGoal(title: String, target: Double, current: Double, deadline: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      val goal = SavingsGoalEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        title = title,
        targetAmount = target,
        currentAmount = current,
        deadline = deadline
      )
      repository.saveSavingsGoal(goal)
    }
  }

  fun addSavingsDeposit(goalId: String, amount: Double) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.addSavingsDeposit(goalId, amount, user.id)
    }
  }

  fun deleteSavingsGoal(goalId: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      repository.deleteSavingsGoal(goalId, user.id)
    }
  }

  // --- Voice Expense Parsing & Confirmation ---
  fun setVoiceListening(isListening: Boolean) {
    _isVoiceListening.value = isListening
  }

  fun processVoiceInput(spokenText: String) {
    _isVoiceListening.value = false
    val parsed = VoiceExpenseParser.parseSpokenText(spokenText)
    if (parsed != null) {
      _voiceConfirmationCandidate.value = parsed
    }
  }

  fun confirmVoiceExpense(parsed: ParsedExpense) {
    addExpense(
      amount = parsed.amount,
      category = parsed.category,
      desc = parsed.description,
      date = parsed.date,
      paymentMode = parsed.paymentMode
    )
    _voiceConfirmationCandidate.value = null
    _currentScreen.value = AppScreen.DASHBOARD
  }

  fun dismissVoiceExpense() {
    _voiceConfirmationCandidate.value = null
  }

  // --- AI Chat ---
  fun sendAiMessage(prompt: String) {
    val user = _currentUser.value ?: return
    if (prompt.isBlank()) return

    viewModelScope.launch {
      val userMsg = ChatMessageEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        sender = "USER",
        message = prompt
      )
      repository.saveChatMessage(userMsg)

      _isAiThinking.value = true

      val txList = transactions.value
      val inc = txList.filter { it.type == "INCOME" }.sumOf { it.amount }
      val exp = txList.filter { it.type == "EXPENSE" }.sumOf { it.amount }

      val reply = GeminiService.askFinancialAssistant(prompt, txList, inc, exp)

      val aiMsg = ChatMessageEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        sender = "AI",
        message = reply
      )
      repository.saveChatMessage(aiMsg)
      _isAiThinking.value = false
    }
  }

  // --- Resources & Location ---
  fun refreshLocation() {
    val mentioned = _userMentionedCity.value
    if (mentioned.isNotBlank()) {
      val coords = LocationService.geocodeAddress(getApplication(), "", mentioned, "")
      _userCoordinates.value = coords
      return
    }
    val user = _currentUser.value
    if (user != null && (user.address.isNotBlank() || user.city.isNotBlank())) {
      val coords = LocationService.geocodeAddress(getApplication(), user.address, user.city, user.state)
      _userCoordinates.value = coords
      return
    }
    val gps = LocationService.getCurrentLocation(getApplication())
    if (gps != null) {
      _userCoordinates.value = gps
      return
    }
    _userCoordinates.value = LocationService.DEFAULT_LOCATION
  }

  fun selectResource(res: ResourceEntity) {
    _selectedResource.value = res
    _currentScreen.value = AppScreen.RESOURCE_DETAIL
  }

  fun reportResource(reason: String, desc: String, onSuccess: () -> Unit) {
    val user = _currentUser.value ?: return
    val res = _selectedResource.value ?: return
    viewModelScope.launch {
      val report = ResourceReportEntity(
        id = UUID.randomUUID().toString(),
        resourceId = res.id,
        resourceName = res.name,
        userId = user.id,
        userEmail = user.email,
        reason = reason,
        description = desc
      )
      repository.submitResourceReport(report)
      onSuccess()
    }
  }

  // --- Articles ---
  fun selectArticle(art: ArticleEntity) {
    _selectedArticle.value = art
    _currentScreen.value = AppScreen.LEARN_DETAIL
  }

  // --- Documents (Real File Handling & Local Storage) ---
  fun uploadDocument(title: String, category: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
      val doc = DocumentEntity(
        id = UUID.randomUUID().toString(),
        userId = user.id,
        fileName = title,
        fileType = "PDF",
        category = category,
        fileSizeFormatted = "1.2 MB",
        storageUri = "internal_vault://${user.id}/$title",
        uploadDate = sdf.format(java.util.Date())
      )
      repository.saveDocument(doc)
    }
  }

  fun uploadRealDocument(
    uri: Uri,
    customTitle: String = "",
    category: String = "Banking",
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val user = _currentUser.value ?: return
    val context = getApplication<Application>()

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        var originalName = "document_${System.currentTimeMillis()}"
        var sizeBytes: Long = 0L

        cursor?.use {
          if (it.moveToFirst()) {
            val nameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = it.getColumnIndex(OpenableColumns.SIZE)
            if (nameIdx >= 0) originalName = it.getString(nameIdx) ?: originalName
            if (sizeIdx >= 0) sizeBytes = it.getLong(sizeIdx)
          }
        }

        // Validate max size (15 MB)
        val maxSizeBytes = 15 * 1024 * 1024
        if (sizeBytes > maxSizeBytes) {
          withContext(Dispatchers.Main) {
            onError("File size exceeds 15 MB limit.")
          }
          return@launch
        }

        // Determine type
        val mimeType = contentResolver.getType(uri) ?: ""
        val extension = when {
          originalName.endsWith(".pdf", ignoreCase = true) || mimeType.contains("pdf") -> "PDF"
          originalName.endsWith(".png", ignoreCase = true) || mimeType.contains("png") -> "PNG"
          originalName.endsWith(".jpg", ignoreCase = true) || originalName.endsWith(".jpeg", ignoreCase = true) || mimeType.contains("jpeg") -> "JPG"
          else -> "PDF"
        }

        val formattedSize = when {
          sizeBytes > 1024 * 1024 -> String.format(Locale.US, "%.1f MB", sizeBytes / (1024.0 * 1024.0))
          sizeBytes > 1024 -> String.format(Locale.US, "%.1f KB", sizeBytes / 1024.0)
          sizeBytes > 0 -> "$sizeBytes B"
          else -> "1.2 MB"
        }

        // Copy file into app-private storage folder
        val userDocsDir = File(context.filesDir, "vault_${user.id}").apply { mkdirs() }
        val targetFile = File(userDocsDir, "${UUID.randomUUID()}_$originalName")
        contentResolver.openInputStream(uri)?.use { input ->
          FileOutputStream(targetFile).use { output ->
            input.copyTo(output)
          }
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val doc = DocumentEntity(
          id = UUID.randomUUID().toString(),
          userId = user.id,
          fileName = customTitle.ifBlank { originalName },
          fileType = extension,
          category = category,
          fileSizeFormatted = formattedSize,
          storageUri = targetFile.absolutePath,
          uploadDate = sdf.format(Date())
        )
        repository.saveDocument(doc)
        withContext(Dispatchers.Main) {
          onSuccess()
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          onError("Failed to upload document: ${e.message}")
        }
      }
    }
  }

  fun deleteDocument(id: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch {
      // Clean up physical file if present
      val doc = documents.value.find { it.id == id }
      if (doc != null && doc.storageUri.isNotBlank()) {
        try {
          val f = File(doc.storageUri)
          if (f.exists()) f.delete()
        } catch (_: Exception) {}
      }
      repository.deleteDocument(id, user.id)
    }
  }

  // --- Notifications ---
  fun markNotificationRead(id: String) {
    viewModelScope.launch { repository.markNotificationRead(id) }
  }

  fun markAllNotificationsRead() {
    val user = _currentUser.value ?: return
    viewModelScope.launch { repository.markAllNotificationsRead(user.id) }
  }

  // --- Admin Operations ---
  fun adminSaveResource(resource: ResourceEntity) {
    viewModelScope.launch { repository.saveResource(resource) }
  }

  fun adminUpdateVerification(id: String, status: String) {
    viewModelScope.launch { repository.updateResourceVerification(id, status) }
  }

  fun adminDeleteResource(id: String) {
    viewModelScope.launch { repository.deleteResource(id) }
  }

  fun adminSaveArticle(article: ArticleEntity) {
    viewModelScope.launch { repository.saveArticle(article) }
  }

  fun adminDeleteArticle(id: String) {
    viewModelScope.launch { repository.deleteArticle(id) }
  }

  fun adminUpdateReport(id: String, status: String, note: String) {
    viewModelScope.launch { repository.updateReport(id, status, note) }
  }

  fun adminBroadcastNotification(title: String, message: String) {
    viewModelScope.launch { repository.sendBroadcastNotification(title, message) }
  }

  fun adminToggleUserStatus(userId: String, currentActive: Boolean) {
    viewModelScope.launch { repository.setUserStatus(userId, !currentActive) }
  }

  fun t(key: String): String {
    return LanguageManager.getString(key, _currentLanguage.value)
  }
}
