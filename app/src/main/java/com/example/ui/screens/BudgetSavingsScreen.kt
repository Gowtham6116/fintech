package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.i18n.tr
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideHeader
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetSavingsScreen(viewModel: FinGuideViewModel) {
  val budgets by viewModel.budgets.collectAsState()
  val savingsGoals by viewModel.savingsGoals.collectAsState()
  val transactions by viewModel.transactions.collectAsState()

  var selectedTab by remember { mutableStateOf(0) } // 0: Budgets, 1: Savings Goals
  var showAddBudgetDialog by remember { mutableStateOf(false) }
  var showAddGoalDialog by remember { mutableStateOf(false) }
  var depositTargetGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }

  val totalMonthlyBudget = budgets.sumOf { it.monthlyLimit }
  val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val overallPct = if (totalMonthlyBudget > 0) ((totalExpense / totalMonthlyBudget) * 100).toInt() else 0

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("budget_savings_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = tr("Budgets & Savings"),
      subtitle = tr("Stay disciplined and achieve goals")
    )

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = PureWhite,
      contentColor = EmeraldPrimary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("${tr("Category Budgets")} (${budgets.size})") }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("${tr("Savings Goals")} (${savingsGoals.size})") }
      )
    }

    if (selectedTab == 0) {
      // BUDGETS TAB
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        // Overall Budget Summary Card
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(tr("Total Monthly Budget"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                  Text(tr("Cap across all categories"), style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                }
                Text(
                  "₹${totalMonthlyBudget.toInt()}",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                )
              }

              LinearProgressIndicator(
                progress = { (overallPct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = if (overallPct > 85) RedExpense else EmeraldPrimary,
                trackColor = Slate100
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${tr("Spent")}: ₹${totalExpense.toInt()} ($overallPct%)", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                Text("${tr("Remaining")}: ₹${(totalMonthlyBudget - totalExpense).toInt().coerceAtLeast(0)}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
              }
            }
          }
        }

        // Add Category Budget Button
        item {
          Button(
            onClick = { showAddBudgetDialog = true },
            modifier = Modifier.fillMaxWidth().testTag("btn_show_add_budget"),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(tr("Set New Category Budget"))
          }
        }

        // List of category budgets
        items(budgets, key = { it.id }) { budget ->
          val catSpent = transactions
            .filter { it.type == "EXPENSE" && it.category.equals(budget.category, ignoreCase = true) }
            .sumOf { it.amount }
          val pct = if (budget.monthlyLimit > 0) ((catSpent / budget.monthlyLimit) * 100).toInt() else 0

          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(tr(budget.category), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                IconButton(onClick = { viewModel.deleteBudget(budget.id) }, modifier = Modifier.size(28.dp)) {
                  Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(18.dp))
                }
              }

              LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (pct >= 100) RedExpense else if (pct >= 85) AmberWarning else EmeraldPrimary,
                trackColor = Slate100
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${tr("Spent")}: ₹${catSpent.toInt()} / ₹${budget.monthlyLimit.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                Text(
                  text = if (pct >= 100) tr("Exceeded!") else "$pct%",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (pct >= 85) RedExpense else EmeraldPrimary
                  )
                )
              }
            }
          }
        }
      }
    } else {
      // SAVINGS GOALS TAB
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        item {
          Button(
            onClick = { showAddGoalDialog = true },
            modifier = Modifier.fillMaxWidth().testTag("btn_show_add_goal"),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Savings, contentDescription = null, tint = Slate900)
            Spacer(modifier = Modifier.width(6.dp))
            Text(tr("Create New Savings Goal"), color = Slate900, fontWeight = FontWeight.Bold)
          }
        }

        if (savingsGoals.isEmpty()) {
          item {
            Box(
              modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(tr("No savings goals yet. Create one to build your emergency fund or dream purchase!"), style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
            }
          }
        } else {
          items(savingsGoals, key = { it.id }) { goal ->
            val pct = if (goal.targetAmount > 0) ((goal.currentAmount / goal.targetAmount) * 100).toInt() else 0
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = PureWhite),
              border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(goal.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                    Text("${tr("Target Deadline")}: ${goal.deadline}", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                  }
                  IconButton(onClick = { viewModel.deleteSavingsGoal(goal.id) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(18.dp))
                  }
                }

                LinearProgressIndicator(
                  progress = { (pct / 100f).coerceIn(0f, 1f) },
                  modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                  color = if (pct >= 100) EmeraldPrimary else GoldAccent,
                  trackColor = Slate100
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("${tr("Saved")}: ₹${goal.currentAmount.toInt()} / ₹${goal.targetAmount.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate700, fontWeight = FontWeight.SemiBold))
                  Text("$pct% ${tr("Achieved")}", style = MaterialTheme.typography.labelMedium.copy(color = if (pct >= 100) EmeraldPrimary else Slate700, fontWeight = FontWeight.Bold))
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  OutlinedButton(
                    onClick = { depositTargetGoal = goal },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ ${tr("Add Deposit")}", fontSize = 12.sp)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Dialog: Add Category Budget
  if (showAddBudgetDialog) {
    var newCat by remember { mutableStateOf("Food") }
    var newLimit by remember { mutableStateOf("") }
    val categories = listOf("Food", "Transport", "Shopping", "Education", "Bills", "Healthcare", "Entertainment", "Other")

    AlertDialog(
      onDismissRequest = { showAddBudgetDialog = false },
      title = { Text(tr("Set Category Budget")) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(tr("Select Category:"), style = MaterialTheme.typography.labelMedium)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.forEach { cat ->
              FilterChip(
                selected = newCat == cat,
                onClick = { newCat = cat },
                label = { Text(tr(cat), fontSize = 11.sp) }
              )
            }
          }
          OutlinedTextField(
            value = newLimit,
            onValueChange = { newLimit = it },
            label = { Text("${tr("Monthly Limit (₹)")} *") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val limit = newLimit.toDoubleOrNull() ?: 0.0
            if (limit > 0) {
              viewModel.saveBudget(newCat, limit)
            }
            showAddBudgetDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text(tr("Set Budget"))
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddBudgetDialog = false }) { Text(tr("Cancel")) }
      }
    )
  }

  // Dialog: Add Savings Goal
  if (showAddGoalDialog) {
    var goalTitle by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }
    var goalInitial by remember { mutableStateOf("") }
    var goalDate by remember { mutableStateOf("2026-12-31") }

    AlertDialog(
      onDismissRequest = { showAddGoalDialog = false },
      title = { Text(tr("New Savings Goal")) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = goalTitle,
            onValueChange = { goalTitle = it },
            label = { Text(tr("Goal Title (e.g. Emergency Fund)")) },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            singleLine = true
          )
          OutlinedTextField(
            value = goalTarget,
            onValueChange = { goalTarget = it },
            label = { Text(tr("Target Amount (₹)")) },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = goalInitial,
            onValueChange = { goalInitial = it },
            label = { Text(tr("Initial Deposit (₹, Optional)")) },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
          OutlinedTextField(
            value = goalDate,
            onValueChange = { goalDate = it },
            label = { Text(tr("Target Deadline (YYYY-MM-DD)")) },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val target = goalTarget.toDoubleOrNull() ?: 0.0
            val initial = goalInitial.toDoubleOrNull() ?: 0.0
            if (goalTitle.isNotBlank() && target > 0) {
              viewModel.saveSavingsGoal(goalTitle, target, initial, goalDate)
            }
            showAddGoalDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text(tr("Create Goal"))
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddGoalDialog = false }) { Text(tr("Cancel")) }
      }
    )
  }

  // Dialog: Add Deposit to Savings Goal
  if (depositTargetGoal != null) {
    var depositAmount by remember { mutableStateOf("") }
    AlertDialog(
      onDismissRequest = { depositTargetGoal = null },
      title = { Text("${tr("Add Deposit")}: ${depositTargetGoal?.title}") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("${tr("Target")}: ₹${depositTargetGoal?.targetAmount?.toInt()}, ${tr("Saved")}: ₹${depositTargetGoal?.currentAmount?.toInt()}")
          OutlinedTextField(
            value = depositAmount,
            onValueChange = { depositAmount = it },
            label = { Text(tr("Deposit Amount (₹)")) },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amt = depositAmount.toDoubleOrNull() ?: 0.0
            if (amt > 0) {
              depositTargetGoal?.let { viewModel.addSavingsDeposit(it.id, amt) }
            }
            depositTargetGoal = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text(tr("Add Deposit"))
        }
      },
      dismissButton = {
        TextButton(onClick = { depositTargetGoal = null }) { Text(tr("Cancel")) }
      }
    )
  }
}
