package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.data.model.UserType
import com.example.data.sync.SyncState
import com.example.i18n.LanguageManager
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)

@Composable
fun DocumentsScreen(viewModel: FinGuideViewModel) {
  val context = LocalContext.current
  val documents by viewModel.documents.collectAsState()
  var showUploadDialog by remember { mutableStateOf(false) }
  var selectedCategory by remember { mutableStateOf("All") }

  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      val cat = if (selectedCategory == "All") "Banking" else selectedCategory
      viewModel.uploadRealDocument(uri = uri, category = cat)
    }
  }

  val categories = listOf("All", "Insurance", "Property", "Banking", "Tax", "Investment", "Other")

  val filteredDocs = remember(documents, selectedCategory) {
    if (selectedCategory == "All") documents
    else documents.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("documents_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Document Vault",
      subtitle = "Encrypted Local Storage"
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { filePickerLauncher.launch("*/*") },
          modifier = Modifier.weight(1f).testTag("btn_upload_document"),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Pick File (PDF/Image)", fontSize = 13.sp)
        }

        OutlinedButton(
          onClick = { showUploadDialog = true },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = EmeraldPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Manual Entry", fontSize = 13.sp, color = EmeraldPrimary)
        }
      }

      // Filter chips
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        categories.take(5).forEach { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }
    }

    if (filteredDocs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Slate300, modifier = Modifier.size(60.dp))
          Text("No documents uploaded yet", style = MaterialTheme.typography.titleMedium.copy(color = Slate600))
          Text("Safely keep your PAN, Insurance policies, or FD receipts in one place.", style = MaterialTheme.typography.bodySmall.copy(color = Slate400))
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        items(filteredDocs, key = { it.id }) { doc ->
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
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EmeraldSurface),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = RedExpense, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(doc.fileName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
                  Text("${doc.category} • ${doc.fileSizeFormatted} • ${doc.uploadedAt}", style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp))
                }
              }

              IconButton(onClick = { viewModel.deleteDocument(doc.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(18.dp))
              }
            }
          }
        }
      }
    }
  }

  // Upload Dialog
  if (showUploadDialog) {
    var docName by remember { mutableStateOf("") }
    var docCat by remember { mutableStateOf("Banking") }

    AlertDialog(
      onDismissRequest = { showUploadDialog = false },
      title = { Text("Upload Financial Document") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = docName,
            onValueChange = { docName = it },
            label = { Text("Document Title (e.g. Life Insurance Policy)") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            singleLine = true
          )
          Text("Category:", style = MaterialTheme.typography.labelSmall)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Insurance", "Property", "Banking", "Tax", "Investment").forEach { c ->
              FilterChip(
                selected = docCat == c,
                onClick = { docCat = c },
                label = { Text(c, fontSize = 11.sp) }
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (docName.isNotBlank()) {
              viewModel.uploadDocument(docName, docCat)
            }
            showUploadDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
        ) {
          Text("Save to Vault")
        }
      },
      dismissButton = {
        TextButton(onClick = { showUploadDialog = false }) { Text("Cancel") }
      }
    )
  }
}

@Composable
fun NotificationsScreen(viewModel: FinGuideViewModel) {
  val notifications by viewModel.notifications.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("notifications_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Notifications & Alerts",
      subtitle = "System & Budget updates"
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
        Text("Mark All as Read", color = EmeraldPrimary, fontSize = 12.sp)
      }
    }

    if (notifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("No notifications right now. You're all caught up!", style = MaterialTheme.typography.bodyMedium.copy(color = Slate500))
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        items(notifications, key = { it.id }) { notif ->
          val icon = when (notif.type) {
            "BUDGET_ALERT" -> Icons.Default.Warning
            "SAVINGS_REMINDER" -> Icons.Default.Savings
            else -> Icons.Default.Notifications
          }
          val iconColor = when (notif.type) {
            "BUDGET_ALERT" -> AmberWarning
            "SAVINGS_REMINDER" -> GoldAccent
            else -> EmeraldPrimary
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.markNotificationRead(notif.id) },
            colors = CardDefaults.cardColors(
              containerColor = if (notif.isRead) PureWhite else Color(0xFFF0FDF4)
            ),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(notif.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
                  if (!notif.isRead) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimary))
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(notif.message, style = MaterialTheme.typography.bodySmall.copy(color = Slate600, lineHeight = 18.sp))
                Spacer(modifier = Modifier.height(4.dp))
                val notifDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(notif.createdAt))
                Text(notifDate, style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp))
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: FinGuideViewModel) {
  val user by viewModel.currentUser.collectAsState()
  val currentLang by viewModel.currentLanguage.collectAsState()
  val forceDesktop by viewModel.forceDesktopLayout.collectAsState()
  val syncState by viewModel.syncState.collectAsState()
  val lastSyncResult by viewModel.lastSyncResult.collectAsState()

  var langDialogExpanded by remember { mutableStateOf(false) }
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var editName by remember(user) { mutableStateOf(user?.name ?: "") }
  var editEmail by remember(user) { mutableStateOf(user?.email ?: "") }
  var editUserType by remember(user) { mutableStateOf(user?.userType ?: UserType.STUDENT.name) }
  var editAddress by remember(user) { mutableStateOf(user?.residentialAddress ?: "") }
  var editCity by remember(user) { mutableStateOf(user?.city ?: "") }
  var editState by remember(user) { mutableStateOf(user?.state ?: "") }
  var editPostalCode by remember(user) { mutableStateOf(user?.postalCode ?: "") }
  var editErrorMessage by remember { mutableStateOf<String?>(null) }
  var syncMessage by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("profile_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Profile & Settings",
      subtitle = "Account preferences & sync"
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // User Profile Card
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  user?.name?.firstOrNull()?.uppercase() ?: "U",
                  style = MaterialTheme.typography.headlineMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              Column {
                Text(user?.name ?: "User", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
                Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                Surface(
                  color = EmeraldSurface,
                  shape = RoundedCornerShape(4.dp),
                  modifier = Modifier.padding(top = 4.dp)
                ) {
                  Text(
                    "${user?.role} • ${user?.userType}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            // Edit Profile Button
            IconButton(
              onClick = {
                editName = user?.name ?: ""
                editEmail = user?.email ?: ""
                editUserType = user?.userType ?: UserType.STUDENT.name
                editAddress = user?.residentialAddress ?: ""
                editCity = user?.city ?: ""
                editState = user?.state ?: ""
                editPostalCode = user?.postalCode ?: ""
                editErrorMessage = null
                showEditProfileDialog = true
              },
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(EmeraldSurface)
                .testTag("btn_edit_profile")
            ) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = EmeraldDark, modifier = Modifier.size(20.dp))
            }
          }

          // Residential Address Display
          if (!user?.residentialAddress.isNullOrBlank() || !user?.city.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Home, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = listOfNotNull(
                  user?.residentialAddress.takeIf { !it.isNullOrBlank() },
                  user?.city.takeIf { !it.isNullOrBlank() },
                  user?.state.takeIf { !it.isNullOrBlank() },
                  user?.postalCode.takeIf { !it.isNullOrBlank() }
                ).joinToString(", "),
                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = {
              editName = user?.name ?: ""
              editEmail = user?.email ?: ""
              editUserType = user?.userType ?: UserType.STUDENT.name
              editAddress = user?.residentialAddress ?: ""
              editCity = user?.city ?: ""
              editState = user?.state ?: ""
              editPostalCode = user?.postalCode ?: ""
              editErrorMessage = null
              showEditProfileDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile (Name, Email, Profession, Address)", color = EmeraldPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
        }
      }

      // Offline-First Cloud Sync Card (Room to Firestore)
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFCCFBF1)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.CloudSync, contentDescription = "Firestore Sync", tint = TealDark, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Room-to-Firestore Cloud Sync", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
                Text("Offline-First Financial Data Synchronization", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
              }
            }

            when (syncState) {
              is SyncState.Syncing -> {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
              }
              is SyncState.Success -> {
                Icon(Icons.Default.CheckCircle, contentDescription = "Synced", tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
              }
              is SyncState.Error -> {
                Icon(Icons.Default.Warning, contentDescription = "Sync Error", tint = RedExpense, modifier = Modifier.size(22.dp))
              }
              else -> {
                Icon(Icons.Default.CloudDone, contentDescription = "Ready", tint = Slate400, modifier = Modifier.size(22.dp))
              }
            }
          }

          // Status detail
          val statusText = when (val state = syncState) {
            is SyncState.Syncing -> state.message
            is SyncState.Success -> state.result.message
            is SyncState.Error -> "Sync Error: ${state.errorMessage}"
            else -> lastSyncResult?.message ?: "Room SQLite database active for zero-latency local operations. Ready to synchronize with Firestore."
          }

          Surface(
            color = Slate50,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              statusText,
              style = MaterialTheme.typography.bodySmall.copy(color = Slate700),
              modifier = Modifier.padding(10.dp)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                viewModel.triggerFirestoreSync { res ->
                  syncMessage = res.message
                }
              },
              modifier = Modifier.weight(1f).testTag("btn_sync_firestore"),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = PureWhite)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Sync to Cloud", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                viewModel.pullFirestoreToRoom { res ->
                  syncMessage = res.message
                }
              },
              modifier = Modifier.weight(1f).testTag("btn_pull_firestore"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = TealDark)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Pull to Room", fontSize = 13.sp, color = TealDark, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Settings Group
      Text("Preferences", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))

      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          // Language Switcher Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { langDialogExpanded = true }
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Translate, contentDescription = null, tint = EmeraldPrimary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("App Language", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("15 Indian Languages Supported", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
              }
            }
            Text(
              LanguageManager.supportedLanguages.find { it.code == currentLang }?.englishName ?: "English",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            )
          }

          HorizontalDivider(color = Slate100)

          // Layout Mode Toggle Row (Simulate Mobile vs Desktop/Tablet)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.toggleLayoutMode() }
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Devices, contentDescription = null, tint = TealSecondary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Cross-Platform Layout", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("Simulate Desktop / Mobile Mode", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
              }
            }
            Text(
              when (forceDesktop) {
                true -> "Desktop (Active)"
                false -> "Mobile (Active)"
                else -> "Auto Responsive"
              },
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = TealSecondary)
            )
          }

          HorizontalDivider(color = Slate100)

          // Document Vault Shortcut
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.navigateTo(AppScreen.DOCUMENTS) }
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Folder, contentDescription = null, tint = GoldAccent)
              Spacer(modifier = Modifier.width(12.dp))
              Text("Secure Document Vault", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
          }
        }
      }

      // Admin Portal Entry (Available for Admin Role)
      if (user?.role == "ADMIN") {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF93C5FD), Color(0xFF93C5FD)))),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF1D4ED8))
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Open Admin Portal", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8)))
                Text("Manage Users, Authorities, & Reports", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF3B82F6)))
              }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF1D4ED8))
          }
        }
      }

      // Logout Button
      Button(
        onClick = { viewModel.requestLogout() },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("btn_logout"),
        colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign Out", fontWeight = FontWeight.Bold)
      }
    }
  }

  // Language Picker Dialog
  if (langDialogExpanded) {
    AlertDialog(
      onDismissRequest = { langDialogExpanded = false },
      title = { Text("Choose Preferred Language") },
      text = {
        LazyColumn(modifier = Modifier.heightIn(max = 350.dp)) {
          items(LanguageManager.supportedLanguages) { lang ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.setLanguage(lang.code)
                  langDialogExpanded = false
                }
                .padding(vertical = 10.dp, horizontal = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(lang.englishName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                Text(lang.nativeName, style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
              }
              if (lang.code == currentLang) {
                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
              }
            }
            HorizontalDivider(color = Slate100)
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { langDialogExpanded = false }) { Text("Close") }
      }
    )
  }

  // Edit Profile Dialog (Allows changing Name, Email, Student/Working Professional/Beginner)
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Edit, contentDescription = null, tint = EmeraldPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Edit Profile Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (editErrorMessage != null) {
            Surface(
              color = Color(0xFFFEE2E2),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                editErrorMessage!!,
                color = RedExpense,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Text("Full Name", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it; editErrorMessage = null },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_name"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Text("Email Address", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
          OutlinedTextField(
            value = editEmail,
            onValueChange = { editEmail = it; editErrorMessage = null },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_email"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Text("Profession / Profile Category", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            UserType.entries.forEach { type ->
              FilterChip(
                selected = editUserType == type.name,
                onClick = { editUserType = type.name },
                label = { Text(type.label) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = EmeraldPrimary,
                  selectedLabelColor = PureWhite
                )
              )
            }
          }

          Text("Residential Address (Street / Area)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
          OutlinedTextField(
            value = editAddress,
            onValueChange = { editAddress = it },
            placeholder = { Text("e.g. 42 Main Street, Indiranagar") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_address"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("City", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
              OutlinedTextField(
                value = editCity,
                onValueChange = { editCity = it },
                placeholder = { Text("Bengaluru") },
                colors = fintechTextFieldColors(),
                textStyle = LocalTextStyle.current.copy(color = Slate900),
                modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_city"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text("State", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
              OutlinedTextField(
                value = editState,
                onValueChange = { editState = it },
                placeholder = { Text("Karnataka") },
                colors = fintechTextFieldColors(),
                textStyle = LocalTextStyle.current.copy(color = Slate900),
                modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_state"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
              )
            }
          }

          Text("Postal / PIN Code", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
          OutlinedTextField(
            value = editPostalCode,
            onValueChange = { editPostalCode = it },
            placeholder = { Text("560038") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth().testTag("input_profile_edit_pincode"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (editName.isBlank() || editEmail.isBlank()) {
              editErrorMessage = "Please provide valid name and email"
              return@Button
            }
            viewModel.updateUserProfile(
              name = editName,
              email = editEmail,
              userType = editUserType,
              address = editAddress,
              city = editCity,
              state = editState,
              postalCode = editPostalCode,
              onSuccess = {
                showEditProfileDialog = false
                editErrorMessage = null
              },
              onError = { err ->
                editErrorMessage = err
              }
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          modifier = Modifier.testTag("btn_save_profile_edit")
        ) {
          Text("Save Changes", color = PureWhite, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel", color = Slate600)
        }
      }
    )
  }
}
