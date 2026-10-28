package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.i18n.LanguageManager
import com.example.i18n.tr
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.voice.AndroidSpeechManager
import com.example.voice.VoiceInputState
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinanceScreen(viewModel: FinGuideViewModel) {
  val transactions by viewModel.transactions.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Expenses, 2: Income
  var selectedFilter by remember { mutableStateOf("All") } // "Today", "This Week", "This Month", "All"
  var searchQuery by remember { mutableStateOf("") }
  var txToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
  var txToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

  val filteredTransactions = remember(transactions, selectedTab, selectedFilter, searchQuery) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Date())

    transactions.filter { tx ->
      // Tab filter
      val tabMatch = when (selectedTab) {
        1 -> tx.type == "EXPENSE"
        2 -> tx.type == "INCOME"
        else -> true
      }
      // Time filter
      val timeMatch = when (selectedFilter) {
        "Today" -> tx.date == todayStr
        "This Month" -> tx.date.startsWith(todayStr.take(7))
        else -> true
      }
      // Search
      val searchMatch = searchQuery.isBlank() ||
        tx.description.contains(searchQuery, ignoreCase = true) ||
        tx.category.contains(searchQuery, ignoreCase = true)

      tabMatch && timeMatch && searchMatch
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("finance_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Financial Ledger",
      subtitle = "${filteredTransactions.size} transactions"
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Primary Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = PureWhite,
        contentColor = EmeraldPrimary
      ) {
        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("All") })
        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Expenses") })
        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Income") })
      }

      // Search & Filters
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search description or category...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("input_search_tx"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Time Filter Chips
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All", "Today", "This Month").forEach { filter ->
          FilterChip(
            selected = selectedFilter == filter,
            onClick = { selectedFilter = filter },
            label = { Text(filter, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }

      // Action Buttons (+ Expense, + Income)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) },
          colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f).testTag("btn_add_expense_finance")
        ) {
          Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Expense", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { viewModel.navigateTo(AppScreen.ADD_INCOME) },
          colors = ButtonDefaults.buttonColors(containerColor = GreenIncome),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f).testTag("btn_add_income_finance")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Income", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // List of Transactions
    if (filteredTransactions.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Slate300, modifier = Modifier.size(64.dp))
          Text("No transactions found", style = MaterialTheme.typography.titleMedium.copy(color = Slate600))
          Text("Try changing search filters or tap '+ Add Expense' above.", style = MaterialTheme.typography.bodySmall.copy(color = Slate400))
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        items(filteredTransactions, key = { it.id }) { tx ->
          val isIncome = tx.type == "INCOME"
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("tx_item_${tx.id}"),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  tx.description,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
                )
                Text(
                  "${tx.category} • ${tx.paymentMode} • ${tx.date}",
                  style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${if (isIncome) "+" else "-"}₹${tx.amount.toInt()}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) GreenIncome else RedExpense
                  )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                  onClick = { txToEdit = tx },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Slate500, modifier = Modifier.size(18.dp))
                }

                IconButton(
                  onClick = { txToDelete = tx },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }
    }

    // Delete confirmation dialog
    if (txToDelete != null) {
      AlertDialog(
        onDismissRequest = { txToDelete = null },
        title = { Text("Delete Transaction") },
        text = { Text("Are you sure you want to delete '${txToDelete?.description}' (₹${txToDelete?.amount?.toInt()})?") },
        confirmButton = {
          Button(
            onClick = {
              txToDelete?.let { viewModel.deleteTransaction(it.id) }
              txToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
          ) {
            Text("Delete")
          }
        },
        dismissButton = {
          TextButton(onClick = { txToDelete = null }) { Text("Cancel") }
        }
      )
    }

    // Edit Transaction Dialog
    if (txToEdit != null) {
      var editAmount by remember { mutableStateOf(txToEdit?.amount?.toInt().toString()) }
      var editDesc by remember { mutableStateOf(txToEdit?.description ?: "") }

      AlertDialog(
        onDismissRequest = { txToEdit = null },
        title = { Text("Edit Transaction") },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = editAmount,
              onValueChange = { editAmount = it },
              label = { Text("Amount (₹)") },
              colors = fintechTextFieldColors(),
              textStyle = LocalTextStyle.current.copy(color = Slate900),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true
            )
            OutlinedTextField(
              value = editDesc,
              onValueChange = { editDesc = it },
              label = { Text("Description") },
              colors = fintechTextFieldColors(),
              textStyle = LocalTextStyle.current.copy(color = Slate900),
              singleLine = true
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val amt = editAmount.toDoubleOrNull() ?: 0.0
              if (amt > 0 && txToEdit != null) {
                viewModel.updateTransaction(
                  txToEdit!!.copy(
                    amount = amt,
                    description = editDesc
                  )
                )
              }
              txToEdit = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
          ) {
            Text("Save")
          }
        },
        dismissButton = {
          TextButton(onClick = { txToEdit = null }) { Text("Cancel") }
        }
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(viewModel: FinGuideViewModel) {
  var amount by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Food") }
  var description by remember { mutableStateOf("") }
  var paymentMode by remember { mutableStateOf("UPI") }

  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  var date by remember { mutableStateOf(sdf.format(Date())) }

  val categories = listOf("Food", "Transport", "Shopping", "Education", "Bills", "Healthcare", "Entertainment", "Other")
  val paymentModes = listOf("UPI", "Cash", "Card", "Bank Transfer", "Other")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("add_expense_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Add Expense",
      subtitle = "Record outgoing spend",
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.FINANCE) }
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Amount Input (Highlighted)
      OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Amount (₹) *") },
        leadingIcon = { Text("₹", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = RedExpense)) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_expense_amount"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
      )

      // Category Selector
      Text("Category *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }

      // Description
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description (e.g. Lunch at cafeteria)") },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_expense_desc"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Date
      OutlinedTextField(
        value = date,
        onValueChange = { date = it },
        label = { Text("Date (YYYY-MM-DD)") },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate500) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Payment Mode
      Text("Payment Mode", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        paymentModes.forEach { mode ->
          FilterChip(
            selected = paymentMode == mode,
            onClick = { paymentMode = mode },
            label = { Text(mode) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealSecondary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Save Button
      Button(
        onClick = {
          val amt = amount.toDoubleOrNull() ?: 0.0
          if (amt > 0) {
            viewModel.addExpense(amt, category, description, date, paymentMode)
            viewModel.navigateTo(AppScreen.FINANCE)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_save_expense"),
        colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("Save Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }

      // Shortcut to Voice Expense
      OutlinedButton(
        onClick = { viewModel.navigateTo(AppScreen.VOICE_EXPENSE) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Record via Voice Instead", color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddIncomeScreen(viewModel: FinGuideViewModel) {
  var amount by remember { mutableStateOf("") }
  var source by remember { mutableStateOf("Salary") }
  var description by remember { mutableStateOf("") }
  var paymentMode by remember { mutableStateOf("Bank Transfer") }

  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  var date by remember { mutableStateOf(sdf.format(Date())) }

  val sources = listOf("Salary", "Part-time", "Pocket Money", "Freelance", "Scholarship", "Other")
  val paymentModes = listOf("Bank Transfer", "UPI", "Cash", "Other")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("add_income_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Add Income",
      subtitle = "Record incoming funds",
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.FINANCE) }
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Amount Input
      OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Amount (₹) *") },
        leadingIcon = { Text("₹", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = GreenIncome)) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_income_amount"),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
      )

      // Source Selector
      Text("Income Source *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sources.forEach { src ->
          FilterChip(
            selected = source == src,
            onClick = { source = src },
            label = { Text(src) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenIncome,
              selectedLabelColor = PureWhite
            )
          )
        }
      }

      // Description
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description (e.g. Monthly Internship Stipend)") },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_income_desc"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Date
      OutlinedTextField(
        value = date,
        onValueChange = { date = it },
        label = { Text("Date (YYYY-MM-DD)") },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Slate500) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Payment Mode
      Text("Payment Mode", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        paymentModes.forEach { mode ->
          FilterChip(
            selected = paymentMode == mode,
            onClick = { paymentMode = mode },
            label = { Text(mode) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TealSecondary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Save Button
      Button(
        onClick = {
          val amt = amount.toDoubleOrNull() ?: 0.0
          if (amt > 0) {
            viewModel.addIncome(amt, source, description, date, paymentMode)
            viewModel.navigateTo(AppScreen.FINANCE)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_save_income"),
        colors = ButtonDefaults.buttonColors(containerColor = GreenIncome),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text("Save Income", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }
  }
}

@Composable
fun VoiceExpenseScreen(viewModel: FinGuideViewModel) {
  val context = LocalContext.current
  var spokenTextInput by remember { mutableStateOf("") }
  val isListening by viewModel.isVoiceListening.collectAsState()
  val candidate by viewModel.voiceConfirmationCandidate.collectAsState()
  val currentLanguage by viewModel.currentLanguage.collectAsState()

  // Real Speech Manager instance
  val speechManager = remember { AndroidSpeechManager(context) }
  val speechState by speechManager.state.collectAsState()
  val liveTranscript by speechManager.liveTranscript.collectAsState()

  // Sync live audio transcript
  LaunchedEffect(liveTranscript) {
    if (liveTranscript.isNotBlank()) {
      spokenTextInput = liveTranscript
    }
  }

  // Handle recognizer state updates
  LaunchedEffect(speechState) {
    when (val s = speechState) {
      is VoiceInputState.Success -> {
        spokenTextInput = s.recognizedText
        viewModel.setVoiceListening(false)
        viewModel.processVoiceInput(s.recognizedText)
      }
      is VoiceInputState.Listening -> {
        viewModel.setVoiceListening(true)
      }
      is VoiceInputState.Error -> {
        viewModel.setVoiceListening(false)
      }
      else -> {}
    }
  }

  // System RecognizerIntent Launcher
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    viewModel.setVoiceListening(false)
    if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
      val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val spoken = matches?.firstOrNull()?.trim()
      if (!spoken.isNullOrBlank()) {
        spokenTextInput = spoken
        viewModel.processVoiceInput(spoken)
      }
    }
  }

  // Audio permission request launcher
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      val langTag = when (currentLanguage) {
        "ta" -> "ta-IN"
        "hi" -> "hi-IN"
        "te" -> "te-IN"
        "kn" -> "kn-IN"
        "ml" -> "ml-IN"
        "bn" -> "bn-IN"
        "mr" -> "mr-IN"
        "gu" -> "gu-IN"
        "pa" -> "pa-IN"
        "or" -> "or-IN"
        "as" -> "as-IN"
        "ur" -> "ur-IN"
        "ne" -> "ne-NP"
        "kok" -> "kok-IN"
        else -> "en-IN"
      }
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your expense (e.g., 250 rupees for lunch)")
      }
      try {
        viewModel.setVoiceListening(true)
        speechLauncher.launch(intent)
      } catch (e: Exception) {
        speechManager.startListening(currentLanguage)
      }
    }
  }

  fun triggerMicListening() {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    } else {
      val langTag = when (currentLanguage) {
        "ta" -> "ta-IN"
        "hi" -> "hi-IN"
        "te" -> "te-IN"
        "kn" -> "kn-IN"
        "ml" -> "ml-IN"
        "bn" -> "bn-IN"
        "mr" -> "mr-IN"
        "gu" -> "gu-IN"
        "pa" -> "pa-IN"
        "or" -> "or-IN"
        "as" -> "as-IN"
        "ur" -> "ur-IN"
        "ne" -> "ne-NP"
        "kok" -> "kok-IN"
        else -> "en-IN"
      }
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your expense (e.g., 250 rupees for lunch)")
      }
      try {
        viewModel.setVoiceListening(true)
        speechLauncher.launch(intent)
      } catch (e: Exception) {
        speechManager.startListening(currentLanguage)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("voice_expense_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Voice Expense",
      subtitle = "Natural speech-to-expense",
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Text(
        LanguageManager.translate("Speak Your Expense", currentLanguage),
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
      )
      Text(
        LanguageManager.translate("Say something like: 'I spent 250 rupees for lunch today' or 'Paid 180 for auto commute'.", currentLanguage),
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate600, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Large Pulse Microphone Action Button
      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(if (isListening) RedExpense.copy(alpha = 0.2f) else EmeraldSurface)
          .clickable {
            if (!isListening) {
              triggerMicListening()
            } else {
              speechManager.stopListening()
              viewModel.setVoiceListening(false)
            }
          }
          .testTag("btn_mic_action"),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(if (isListening) RedExpense else EmeraldPrimary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
            contentDescription = "Voice Input",
            tint = PureWhite,
            modifier = Modifier.size(38.dp)
          )
        }
      }

      Text(
        text = if (isListening) LanguageManager.translate("Listening... Tap to stop", currentLanguage) else LanguageManager.translate("Tap microphone to speak", currentLanguage),
        style = MaterialTheme.typography.labelMedium.copy(
          color = if (isListening) RedExpense else EmeraldPrimary,
          fontWeight = FontWeight.Bold
        )
      )

      // Live status message if speech state has an error
      if (speechState is VoiceInputState.Error) {
        Text(
          text = (speechState as VoiceInputState.Error).errorMessage,
          style = MaterialTheme.typography.bodySmall.copy(color = RedExpense),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }

      HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))

      // Quick voice suggestion chips
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          LanguageManager.translate("Try Example Spoken Phrases:", currentLanguage),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate500)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Tea ₹20", "Lunch ₹250", "Auto ₹150", "Petrol ₹500").forEach { phrase ->
            SuggestionChip(
              onClick = {
                val fullPhrase = when (phrase) {
                  "Tea ₹20" -> "Tea 20 rupees"
                  "Lunch ₹250" -> "I spent 250 for lunch"
                  "Auto ₹150" -> "Paid 150 for auto commute"
                  "Petrol ₹500" -> "Petrol 500 rupees by UPI"
                  else -> phrase
                }
                spokenTextInput = fullPhrase
                viewModel.processVoiceInput(fullPhrase)
              },
              label = { Text(phrase, fontSize = 12.sp) }
            )
          }
        }
      }

      HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))

      // Text Input Fallback (Required by prompt)
      Text(
        LanguageManager.translate("Or type spoken phrase manually (Text Fallback):", currentLanguage),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate700)
      )

      OutlinedTextField(
        value = spokenTextInput,
        onValueChange = { spokenTextInput = it },
        placeholder = { Text("e.g. Paid 350 for movie ticket") },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_voice_fallback"),
        shape = RoundedCornerShape(12.dp)
      )

      Button(
        onClick = {
          if (spokenTextInput.isNotBlank()) {
            viewModel.processVoiceInput(spokenTextInput)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("btn_parse_voice"),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(LanguageManager.translate("Parse & Review Expense", currentLanguage), fontWeight = FontWeight.Bold)
      }
    }
  }

  // Confirmation Screen / Dialog
  if (candidate != null) {
    VoiceExpenseConfirmationDialog(
      candidate = candidate!!,
      onConfirm = { viewModel.confirmVoiceExpense(candidate!!) },
      onEdit = {
        viewModel.dismissVoiceExpense()
        viewModel.navigateTo(AppScreen.ADD_EXPENSE)
      },
      onCancel = { viewModel.dismissVoiceExpense() }
    )
  }
}
