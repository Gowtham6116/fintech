package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.i18n.tr
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
  viewModel: FinGuideViewModel,
  isDesktopView: Boolean = false
) {
  val user by viewModel.currentUser.collectAsState()
  val transactions by viewModel.transactions.collectAsState()
  val budgets by viewModel.budgets.collectAsState()
  val savingsGoals by viewModel.savingsGoals.collectAsState()

  // Compute Aggregates
  val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
  val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val totalBalance = totalIncome - totalExpense
  val totalSavings = savingsGoals.sumOf { it.currentAmount }

  val categoryTotals = transactions
    .filter { it.type == "EXPENSE" }
    .groupBy { it.category }
    .mapValues { entry -> entry.value.sumOf { it.amount } }

  val totalMonthlyBudget = budgets.sumOf { it.monthlyLimit }
  val budgetUsedPct = if (totalMonthlyBudget > 0) ((totalExpense / totalMonthlyBudget) * 100).toInt() else 0

  // Dynamic AI Insight from real ledger
  val highestCategory = categoryTotals.maxByOrNull { it.value }
  val aiInsight = remember(transactions, highestCategory) {
    if (highestCategory != null && totalExpense > 0) {
      val pct = (highestCategory.value / totalExpense * 100).toInt()
      "You spent $pct% of your total budget on ${highestCategory.key} (₹${highestCategory.value.toInt()}). Consider setting a specific budget cap for ${highestCategory.key} to save more this month."
    } else {
      "Welcome to FinGuide! Start logging your daily expenses or tap Voice Expense to build your personalized financial insights."
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("dashboard_screen")
  ) {
    // Header
    FinGuideHeader(
      viewModel = viewModel,
      title = "${tr("Welcome back,")} ${user?.name?.split(" ")?.firstOrNull() ?: tr("User")}!",
      subtitle = tr("Let's plan a better tomorrow.")
    )

    if (isDesktopView) {
      // DESKTOP / TABLET MULTI-COLUMN LAYOUT
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Left Column: KPIs + Quick Actions + Spending Chart
        Column(
          modifier = Modifier
            .weight(1.2f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Top Dashboard Dark Banner with Crisp Pure White Text
          DashboardTopBanner(
            userName = user?.name ?: tr("Valued Member"),
            userType = user?.userType ?: "STUDENT",
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense
          )

          // 4 KPI Cards in 2x2 Grid
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
              title = tr("Total Balance"),
              amount = "₹${totalBalance.toInt()}",
              subtitle = tr("Net Available"),
              icon = Icons.Default.AccountBalance,
              accentColor = EmeraldPrimary,
              backgroundColor = PureWhite,
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = tr("Income"),
              amount = "₹${totalIncome.toInt()}",
              subtitle = "${transactions.count { it.type == "INCOME" }} ${tr("entries")}",
              icon = Icons.Default.TrendingUp,
              accentColor = GreenIncome,
              backgroundColor = PureWhite,
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
              title = tr("Expenses"),
              amount = "₹${totalExpense.toInt()}",
              subtitle = "${transactions.count { it.type == "EXPENSE" }} ${tr("entries")}",
              icon = Icons.Default.TrendingDown,
              accentColor = RedExpense,
              backgroundColor = PureWhite,
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = tr("Savings Goal"),
              amount = "₹${totalSavings.toInt()}",
              subtitle = "${savingsGoals.size} ${tr("active goals")}",
              icon = Icons.Default.Savings,
              accentColor = GoldAccent,
              backgroundColor = PureWhite,
              modifier = Modifier.weight(1f)
            )
          }

          // Quick Action Bar
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              QuickActionButton(
                title = tr("+ Expense"),
                icon = Icons.Default.AddCircleOutline,
                color = RedExpense,
                onClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) }
              )
              QuickActionButton(
                title = tr("+ Income"),
                icon = Icons.Default.ArrowCircleUp,
                color = GreenIncome,
                onClick = { viewModel.navigateTo(AppScreen.ADD_INCOME) }
              )
              QuickActionButton(
                title = tr("Budget"),
                icon = Icons.Default.PieChart,
                color = TealSecondary,
                onClick = { viewModel.navigateTo(AppScreen.BUDGET_SAVINGS) }
              )
              QuickActionButton(
                title = tr("Savings"),
                icon = Icons.Default.Savings,
                color = GoldAccent,
                onClick = { viewModel.navigateTo(AppScreen.BUDGET_SAVINGS) }
              )
              QuickActionButton(
                title = tr("Voice"),
                icon = Icons.Default.Mic,
                color = EmeraldPrimary,
                onClick = { viewModel.navigateTo(AppScreen.VOICE_EXPENSE) }
              )
            }
          }

          // Category Spending Breakdown Chart
          CategorySpendingChart(
            categoryTotals = categoryTotals,
            totalExpense = totalExpense
          )
        }

        // Right Column: AI Insights + Budget Progress + Recent Transactions
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // AI Insight Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldLight, EmeraldLight)))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(tr("FinGuide AI Insight"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(aiInsight, style = MaterialTheme.typography.bodySmall.copy(color = Slate800, lineHeight = 18.sp))
            }
          }

          // Budget Progress Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tr("Monthly Budget Progress"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text("$budgetUsedPct% ${tr("Used")}", style = MaterialTheme.typography.labelMedium.copy(color = if (budgetUsedPct > 85) RedExpense else EmeraldPrimary, fontWeight = FontWeight.Bold))
              }
              LinearProgressIndicator(
                progress = { (budgetUsedPct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (budgetUsedPct > 85) RedExpense else EmeraldPrimary,
                trackColor = Slate100
              )
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${tr("Spent")}: ₹${totalExpense.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                Text("${tr("Limit")}: ₹${totalMonthlyBudget.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
              }
            }
          }

          // Recent Transactions List
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(tr("Recent Transactions"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                TextButton(onClick = { viewModel.navigateTo(AppScreen.FINANCE) }) {
                  Text(tr("View All"), fontSize = 12.sp, color = EmeraldPrimary)
                }
              }

              if (transactions.isEmpty()) {
                Text(tr("No transactions logged yet."), style = MaterialTheme.typography.bodySmall.copy(color = Slate400), modifier = Modifier.padding(vertical = 12.dp))
              } else {
                transactions.take(5).forEach { tx ->
                  TransactionRowItem(tx = tx)
                  HorizontalDivider(color = Slate100)
                }
              }
            }
          }
        }
      }
    } else {
      // MOBILE COMPACT TOUCH LAYOUT
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
      ) {
        // Top Dashboard Dark Banner with Crisp Pure White Text
        item {
          DashboardTopBanner(
            userName = user?.name ?: tr("Valued Member"),
            userType = user?.userType ?: "STUDENT",
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense
          )
        }

        // KPI Cards Row (2x2)
        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              KpiCard(
                title = tr("Total Balance"),
                amount = "₹${totalBalance.toInt()}",
                subtitle = tr("Available Funds"),
                icon = Icons.Default.AccountBalance,
                accentColor = EmeraldPrimary,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = tr("Income"),
                amount = "₹${totalIncome.toInt()}",
                subtitle = "${transactions.count { it.type == "INCOME" }} ${tr("entries")}",
                icon = Icons.Default.TrendingUp,
                accentColor = GreenIncome,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              KpiCard(
                title = tr("Expenses"),
                amount = "₹${totalExpense.toInt()}",
                subtitle = "${transactions.count { it.type == "EXPENSE" }} ${tr("entries")}",
                icon = Icons.Default.TrendingDown,
                accentColor = RedExpense,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = tr("Savings Goal"),
                amount = "₹${totalSavings.toInt()}",
                subtitle = "${savingsGoals.size} ${tr("active goals")}",
                icon = Icons.Default.Savings,
                accentColor = GoldAccent,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Quick Actions Row
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              QuickActionButton(
                title = tr("+ Expense"),
                icon = Icons.Default.AddCircleOutline,
                color = RedExpense,
                onClick = { viewModel.navigateTo(AppScreen.ADD_EXPENSE) }
              )
              QuickActionButton(
                title = tr("+ Income"),
                icon = Icons.Default.ArrowCircleUp,
                color = GreenIncome,
                onClick = { viewModel.navigateTo(AppScreen.ADD_INCOME) }
              )
              QuickActionButton(
                title = tr("Budget"),
                icon = Icons.Default.PieChart,
                color = TealSecondary,
                onClick = { viewModel.navigateTo(AppScreen.BUDGET_SAVINGS) }
              )
              QuickActionButton(
                title = tr("Savings"),
                icon = Icons.Default.Savings,
                color = GoldAccent,
                onClick = { viewModel.navigateTo(AppScreen.BUDGET_SAVINGS) }
              )
              QuickActionButton(
                title = tr("Voice"),
                icon = Icons.Default.Mic,
                color = EmeraldPrimary,
                onClick = { viewModel.navigateTo(AppScreen.VOICE_EXPENSE) }
              )
            }
          }
        }

        // AI Insight Banner
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldLight, EmeraldLight)))
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(EmeraldPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(tr("FinGuide AI Insight"), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldDark))
                Spacer(modifier = Modifier.height(2.dp))
                Text(aiInsight, style = MaterialTheme.typography.bodySmall.copy(color = Slate800, lineHeight = 18.sp))
              }
            }
          }
        }

        // Monthly Spending Breakdown
        item {
          CategorySpendingChart(
            categoryTotals = categoryTotals,
            totalExpense = totalExpense
          )
        }

        // Budget Progress
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tr("Monthly Budget Progress"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text("$budgetUsedPct% ${tr("Used")}", style = MaterialTheme.typography.labelMedium.copy(color = if (budgetUsedPct > 85) RedExpense else EmeraldPrimary, fontWeight = FontWeight.Bold))
              }
              LinearProgressIndicator(
                progress = { (budgetUsedPct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (budgetUsedPct > 85) RedExpense else EmeraldPrimary,
                trackColor = Slate100
              )
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${tr("Spent")}: ₹${totalExpense.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                Text("${tr("Remaining")}: ₹${(totalMonthlyBudget - totalExpense).toInt().coerceAtLeast(0)}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
              }
            }
          }
        }

        // Recent Transactions Section
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(tr("Recent Transactions"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
            TextButton(
              onClick = { viewModel.navigateTo(AppScreen.FINANCE) },
              modifier = Modifier.testTag("btn_see_all_transactions")
            ) {
              Text(tr("View All"), fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
          }
        }

        if (transactions.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
              contentAlignment = Alignment.Center
            ) {
              Text("No transactions logged yet. Tap '+ Expense' to begin!", style = MaterialTheme.typography.bodySmall.copy(color = Slate400))
            }
          }
        } else {
          items(transactions.take(5)) { tx ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = PureWhite),
              border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
            ) {
              TransactionRowItem(tx = tx)
            }
          }
        }
      }
    }
  }
}

@Composable
fun TransactionRowItem(tx: TransactionEntity) {
  val isIncome = tx.type == "INCOME"
  val categoryIcon = when (tx.category.lowercase()) {
    "food" -> Icons.Default.Restaurant
    "transport" -> Icons.Default.DirectionsCar
    "shopping" -> Icons.Default.ShoppingBag
    "education" -> Icons.Default.School
    "bills" -> Icons.Default.Receipt
    "healthcare" -> Icons.Default.MedicalServices
    "entertainment" -> Icons.Default.ConfirmationNumber
    "salary" -> Icons.Default.Work
    "freelance" -> Icons.Default.Laptop
    else -> Icons.Default.AccountBalanceWallet
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (isIncome) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          categoryIcon,
          contentDescription = tx.category,
          tint = if (isIncome) GreenIncome else RedExpense,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          tx.description,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate900),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          "${tx.category} • ${tx.date} • ${tx.paymentMode}",
          style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp),
          maxLines = 1
        )
      }
    }

    Text(
      text = "${if (isIncome) "+" else "-"}₹${tx.amount.toInt()}",
      style = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.Bold,
        color = if (isIncome) GreenIncome else RedExpense
      )
    )
  }
}

@Composable
fun DashboardTopBanner(
  userName: String,
  userType: String,
  totalBalance: Double,
  totalIncome: Double,
  totalExpense: Double
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_top_banner"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            listOf(
              Color(0xFF064E3B), // Dark emerald
              Color(0xFF065F46), // Emerald
              Color(0xFF0F172A)  // Dark slate
            )
          )
        )
        .padding(18.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = tr("Welcome back,"),
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
              )
            )
            Text(
              text = userName,
              style = MaterialTheme.typography.titleLarge.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
              )
            )
          }
          Surface(
            color = Color.White.copy(alpha = 0.2f),
            shape = RoundedCornerShape(20.dp)
          ) {
            Text(
              text = tr(userType),
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = tr("Net Available Balance"),
          style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
        )
        Text(
          text = "₹${totalBalance.toInt()}",
          style = MaterialTheme.typography.headlineMedium.copy(
            color = PureWhite,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("${tr("Income")}: ₹${totalIncome.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite))
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("${tr("Expenses")}: ₹${totalExpense.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite))
          }
        }
      }
    }
  }
}
