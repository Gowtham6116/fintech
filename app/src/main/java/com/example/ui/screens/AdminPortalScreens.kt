package com.example.ui.screens

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
import com.example.data.model.ArticleEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.ResourceReportEntity
import com.example.data.model.User
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideHeader
import com.example.ui.components.KpiCard
import com.example.ui.components.VerificationBadge
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*
import java.util.UUID

@Composable
fun AdminPortalScreen(viewModel: FinGuideViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val users by viewModel.allUsersAdmin.collectAsState()
  val resources by viewModel.allResourcesAdmin.collectAsState()
  val articles by viewModel.allArticlesAdmin.collectAsState()
  val reports by viewModel.allReportsAdmin.collectAsState()

  var adminSubTab by remember { mutableStateOf(0) } // 0: Dashboard, 1: Users, 2: Resources, 3: Reports, 4: Broadcast

  // Dialogs
  var showAddResourceDialog by remember { mutableStateOf(false) }
  var showBroadcastDialog by remember { mutableStateOf(false) }
  var activeReportForReview by remember { mutableStateOf<ResourceReportEntity?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("admin_portal_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "FinGuide Admin Portal",
      subtitle = "Government Registry & User Administration",
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
    )

    // Admin Navigation Tab Row
    ScrollableTabRow(
      selectedTabIndex = adminSubTab,
      containerColor = PureWhite,
      contentColor = EmeraldPrimary,
      edgePadding = 12.dp
    ) {
      Tab(selected = adminSubTab == 0, onClick = { adminSubTab = 0 }, text = { Text("Dashboard") })
      Tab(selected = adminSubTab == 1, onClick = { adminSubTab = 1 }, text = { Text("Users (${users.size})") })
      Tab(selected = adminSubTab == 2, onClick = { adminSubTab = 2 }, text = { Text("Resources (${resources.size})") })
      Tab(selected = adminSubTab == 3, onClick = { adminSubTab = 3 }, text = { Text("Reports (${reports.size})") })
      Tab(selected = adminSubTab == 4, onClick = { adminSubTab = 4 }, text = { Text("Broadcast") })
    }

    when (adminSubTab) {
      0 -> {
        // ADMIN DASHBOARD OVERVIEW
        val activeUsers = users.count { it.isActive }
        val verifiedResources = resources.count { it.verificationStatus == "VERIFIED" }
        val pendingResources = resources.count { it.verificationStatus != "VERIFIED" }
        val pendingReports = reports.count { it.status == "PENDING" }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          item {
            Text("Platform Health & Registry Metrics", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
          }

          // KPI Grid
          item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              KpiCard(
                title = "Total Users",
                amount = "${users.size}",
                subtitle = "$activeUsers active accounts",
                icon = Icons.Default.People,
                accentColor = Color(0xFF2563EB),
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = "Verified Orgs",
                amount = "$verifiedResources",
                subtitle = "SEBI / RBI / BIS / IBBI",
                icon = Icons.Default.Verified,
                accentColor = EmeraldPrimary,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
            }
          }

          item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              KpiCard(
                title = "Needs Recheck",
                amount = "$pendingResources",
                subtitle = "Pending review",
                icon = Icons.Default.Warning,
                accentColor = AmberWarning,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
              KpiCard(
                title = "User Reports",
                amount = "$pendingReports",
                subtitle = "Active flags",
                icon = Icons.Default.ReportProblem,
                accentColor = RedExpense,
                backgroundColor = PureWhite,
                modifier = Modifier.weight(1f)
              )
            }
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = PureWhite),
              border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Authoritative Distribution", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                val authorities = listOf("RBI", "SEBI", "IBBI", "BIS", "TNREGINET", "IRDAI")
                authorities.forEach { auth ->
                  val count = resources.count { it.authority == auth }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(auth, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = Slate700))
                    Text("$count listings", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                  }
                  HorizontalDivider(color = Slate100)
                }
              }
            }
          }
        }
      }

      1 -> {
        // USER MANAGEMENT
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(users, key = { it.id }) { u ->
            Card(
              modifier = Modifier.fillMaxWidth(),
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
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(u.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      color = if (u.isActive) EmeraldSurface else Color(0xFFFEE2E2),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        if (u.isActive) "ACTIVE" else "DEACTIVATED",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = if (u.isActive) EmeraldDark else RedExpense,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text("${u.email} • Role: ${u.role} • ${u.userType}", style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp))
                  Text("Lang: ${u.preferredLanguage.uppercase()}", style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp))
                }

                Switch(
                  checked = u.isActive,
                  onCheckedChange = { viewModel.adminToggleUserStatus(u.id, u.isActive) },
                  colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                )
              }
            }
          }
        }
      }

      2 -> {
        // RESOURCE MANAGEMENT
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
          Button(
            onClick = { showAddResourceDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Verified Authority")
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
          ) {
            items(resources, key = { it.id }) { res ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(res.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900), modifier = Modifier.weight(1f))
                    VerificationBadge(status = res.verificationStatus)
                  }

                  Text("Authority: ${res.authority} • Reg: ${res.registrationNumber}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                  Text("${res.address}, ${res.city}", style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    if (res.verificationStatus != "VERIFIED") {
                      TextButton(onClick = { viewModel.adminUpdateVerification(res.id, "VERIFIED") }) {
                        Text("Approve Verified", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    } else {
                      TextButton(onClick = { viewModel.adminUpdateVerification(res.id, "NEEDS_RECHECK") }) {
                        Text("Flag Recheck", color = AmberWarning, fontSize = 12.sp)
                      }
                    }

                    IconButton(onClick = { viewModel.adminDeleteResource(res.id) }, modifier = Modifier.size(28.dp)) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }
        }
      }

      3 -> {
        // REPORTS REVIEW
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          if (reports.isEmpty()) {
            item {
              Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                Text("No user reports submitted.", style = MaterialTheme.typography.bodyMedium.copy(color = Slate500))
              }
            }
          } else {
            items(reports, key = { it.id }) { rep ->
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
                    Text(rep.resourceName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
                    Surface(
                      color = if (rep.status == "PENDING") Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        rep.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = if (rep.status == "PENDING") AmberWarning else EmeraldDark,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Text("Reason: ${rep.reason}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = RedExpense))
                  if (rep.description.isNotBlank()) {
                    Text("User note: ${rep.description}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
                  }
                  Text("Reported by: ${rep.userEmail} on ${rep.createdAt}", style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp))

                  if (rep.status == "PENDING") {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.End
                    ) {
                      OutlinedButton(
                        onClick = {
                          viewModel.adminUpdateReport(rep.id, "REJECTED", "Information confirmed verified with authority.")
                        },
                        modifier = Modifier.padding(end = 8.dp)
                      ) {
                        Text("Dismiss", fontSize = 11.sp)
                      }
                      Button(
                        onClick = {
                          viewModel.adminUpdateReport(rep.id, "RESOLVED", "Resource details updated and flagged for re-inspection.")
                          viewModel.adminUpdateVerification(rep.resourceId, "NEEDS_RECHECK")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                      ) {
                        Text("Resolve & Flag", fontSize = 11.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      4 -> {
        // BROADCAST ANNOUNCEMENTS
        var bTitle by remember { mutableStateOf("") }
        var bMessage by remember { mutableStateOf("") }
        var broadcastSent by remember { mutableStateOf(false) }

        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text("Send Broadcast Announcement", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text(
            "Broadcast high-priority financial notices, regulatory warnings, or platform updates to all registered users.",
            style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
          )

          if (broadcastSent) {
            Surface(color = EmeraldSurface, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
              Text("Broadcast notification sent successfully!", color = EmeraldDark, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
          }

          OutlinedTextField(
            value = bTitle,
            onValueChange = { bTitle = it; broadcastSent = false },
            label = { Text("Notification Title") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          OutlinedTextField(
            value = bMessage,
            onValueChange = { bMessage = it; broadcastSent = false },
            label = { Text("Message Body") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
          )

          Button(
            onClick = {
              if (bTitle.isNotBlank() && bMessage.isNotBlank()) {
                viewModel.adminBroadcastNotification(bTitle, bMessage)
                bTitle = ""
                bMessage = ""
                broadcastSent = true
              }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Campaign, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Send Announcement to All Users", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  // Dialog: Add New Resource
  if (showAddResourceDialog) {
    var rName by remember { mutableStateOf("") }
    var rCat by remember { mutableStateOf("Banks") }
    var rAddress by remember { mutableStateOf("") }
    var rCity by remember { mutableStateOf("Chennai") }
    var rPhone by remember { mutableStateOf("+91 44 ") }
    var rAuthority by remember { mutableStateOf("RBI") }
    var rRegNumber by remember { mutableStateOf("") }
    var rUrl by remember { mutableStateOf("https://www.rbi.org.in") }

    AlertDialog(
      onDismissRequest = { showAddResourceDialog = false },
      title = { Text("Register Official Authority") },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(value = rName, onValueChange = { rName = it }, label = { Text("Name") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rAddress, onValueChange = { rAddress = it }, label = { Text("Address") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rCity, onValueChange = { rCity = it }, label = { Text("City") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rPhone, onValueChange = { rPhone = it }, label = { Text("Phone") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rAuthority, onValueChange = { rAuthority = it }, label = { Text("Authority (SEBI, RBI, IBBI, BIS, TNREGINET)") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rRegNumber, onValueChange = { rRegNumber = it }, label = { Text("Registration Number") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
          OutlinedTextField(value = rUrl, onValueChange = { rUrl = it }, label = { Text("Verification URL") }, colors = fintechTextFieldColors(), textStyle = LocalTextStyle.current.copy(color = Slate900), singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (rName.isNotBlank() && rRegNumber.isNotBlank()) {
              viewModel.adminSaveResource(
                ResourceEntity(
                  id = UUID.randomUUID().toString(),
                  name = rName,
                  category = rCat,
                  address = rAddress,
                  city = rCity,
                  state = "Tamil Nadu",
                  phone = rPhone,
                  latitude = 13.0827,
                  longitude = 80.2707,
                  authority = rAuthority,
                  registrationNumber = rRegNumber,
                  verificationStatus = "VERIFIED",
                  sourceUrl = rUrl,
                  lastVerifiedAt = "2026-09-20"
                )
              )
            }
            showAddResourceDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text("Save Resource")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddResourceDialog = false }) { Text("Cancel") }
      }
    )
  }
}
