@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ResourceEntity
import com.example.i18n.tr
import com.example.i18n.LanguageManager
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.theme.*
import com.example.voice.ParsedExpense

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinGuideHeader(
  viewModel: FinGuideViewModel,
  title: String,
  subtitle: String? = null,
  showBack: Boolean = false,
  onBack: () -> Unit = {}
) {
  val currentLang by viewModel.currentLanguage.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val unreadCount = notifications.count { !it.isRead }
  var langMenuExpanded by remember { mutableStateOf(false) }

  val displayTitle = remember(title, currentLang) {
    LanguageManager.translate(title, currentLang)
  }
  val displaySubtitle = remember(subtitle, currentLang) {
    subtitle?.let { LanguageManager.translate(it, currentLang) }
  }

  Surface(
    color = Color(0xFF064E3B),
    contentColor = PureWhite,
    tonalElevation = 4.dp,
    shadowElevation = 3.dp
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xFF064E3B), // Emerald 900
              Color(0xFF065F46), // Emerald 800
              Color(0xFF047857)  // Emerald 700
            )
          )
        )
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          if (showBack) {
            IconButton(
              onClick = { viewModel.toggleMenuBar() },
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .testTag("btn_menubar")
            ) {
              Icon(
                Icons.Default.Menu,
                contentDescription = "Open Menubar",
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
              onClick = onBack,
              modifier = Modifier.testTag("btn_back")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PureWhite)
            }
            Spacer(modifier = Modifier.width(6.dp))
          } else {
            IconButton(
              onClick = { viewModel.toggleMenuBar() },
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .testTag("btn_menubar")
            ) {
              Icon(
                Icons.Default.Menu,
                contentDescription = "Open Menubar",
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
          }

          Column {
            Text(
              text = displayTitle,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (displaySubtitle != null) {
              Text(
                text = displaySubtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f)),
                maxLines = 1
              )
            }
          }
        }

        // Header Action Controls: Language Switcher, Cloud Sync, Layout Toggle, Notification Bell
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Language Switcher Dropdown
          Box {
            FilledTonalButton(
              onClick = { langMenuExpanded = true },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier
                .height(32.dp)
                .testTag("btn_language_selector"),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.White.copy(alpha = 0.2f),
                contentColor = PureWhite
              )
            ) {
              Icon(Icons.Default.Translate, contentDescription = "Language", tint = PureWhite, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = LanguageManager.supportedLanguages.find { it.code == currentLang }?.code?.uppercase() ?: "EN",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
              )
            }

            DropdownMenu(
              expanded = langMenuExpanded,
              onDismissRequest = { langMenuExpanded = false }
            ) {
              LanguageManager.supportedLanguages.forEach { lang ->
                DropdownMenuItem(
                  text = {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text("${lang.englishName} (${lang.nativeName})", color = Slate900)
                      if (lang.code == currentLang) {
                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = EmeraldPrimary)
                      }
                    }
                  },
                  onClick = {
                    viewModel.setLanguage(lang.code)
                    langMenuExpanded = false
                  }
                )
              }
            }
          }

          // Cloud Sync Trigger Button (Room-to-Firestore)
          IconButton(
            onClick = { viewModel.triggerFirestoreSync() },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("btn_cloud_sync")
          ) {
            Icon(
              Icons.Default.Sync,
              contentDescription = "Sync Room with Firestore",
              tint = PureWhite,
              modifier = Modifier.size(18.dp)
            )
          }

          // Responsive Layout Toggle (Simulates Mobile / Tablet / Desktop)
          val forceDesktop by viewModel.forceDesktopLayout.collectAsState()
          IconButton(
            onClick = { viewModel.toggleLayoutMode() },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("btn_toggle_layout")
          ) {
            Icon(
              imageVector = if (forceDesktop == true) Icons.Default.Laptop else Icons.Default.Smartphone,
              contentDescription = "Toggle Responsive Mode",
              tint = PureWhite,
              modifier = Modifier.size(18.dp)
            )
          }

          // Notifications
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("btn_notifications")
          ) {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(containerColor = RedExpense) { Text("$unreadCount", color = PureWhite) }
                }
              }
            ) {
              Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = PureWhite, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }
}

/**
 * Standard fintech text field colors ensuring typed text is ALWAYS crisp high-contrast black/slate-900.
 */
@Composable
fun fintechTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = Slate900,
  unfocusedTextColor = Slate900,
  disabledTextColor = Slate700,
  errorTextColor = RedExpense,
  focusedContainerColor = PureWhite,
  unfocusedContainerColor = PureWhite,
  disabledContainerColor = Slate100,
  errorContainerColor = PureWhite,
  cursorColor = Slate900,
  errorCursorColor = RedExpense,
  focusedBorderColor = EmeraldPrimary,
  unfocusedBorderColor = Slate300,
  disabledBorderColor = Slate200,
  errorBorderColor = RedExpense,
  focusedLeadingIconColor = EmeraldPrimary,
  unfocusedLeadingIconColor = Slate500,
  focusedTrailingIconColor = EmeraldPrimary,
  unfocusedTrailingIconColor = Slate500,
  focusedLabelColor = EmeraldPrimary,
  unfocusedLabelColor = Slate600,
  focusedPlaceholderColor = Slate400,
  unfocusedPlaceholderColor = Slate400
)

@Composable
fun fintechStandardTextFieldColors() = TextFieldDefaults.colors(
  focusedTextColor = Slate900,
  unfocusedTextColor = Slate900,
  disabledTextColor = Slate700,
  errorTextColor = RedExpense,
  focusedContainerColor = PureWhite,
  unfocusedContainerColor = PureWhite,
  disabledContainerColor = Slate100,
  errorContainerColor = PureWhite,
  cursorColor = Slate900,
  errorCursorColor = RedExpense,
  focusedIndicatorColor = EmeraldPrimary,
  unfocusedIndicatorColor = Slate300,
  focusedLabelColor = EmeraldPrimary,
  unfocusedLabelColor = Slate600,
  focusedPlaceholderColor = Slate400,
  unfocusedPlaceholderColor = Slate400
)

@Composable
fun KpiCard(
  title: String,
  amount: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  backgroundColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .testTag("kpi_card_${title.lowercase().replace(" ", "_")}"),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium.copy(
            color = Slate600,
            fontWeight = FontWeight.SemiBold
          )
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = amount,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          color = Slate900,
          letterSpacing = (-0.5).sp
        )
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Slate500,
          fontSize = 11.sp
        )
      )
    }
  }
}

@Composable
fun QuickActionButton(
  title: String,
  icon: ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clickable(onClick = onClick)
      .padding(4.dp)
      .testTag("quick_action_${title.lowercase().replace(" ", "_")}")
  ) {
    Box(
      modifier = Modifier
        .size(50.dp)
        .shadow(2.dp, CircleShape)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        color = Slate700,
        textAlign = TextAlign.Center
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
fun CategorySpendingChart(
  categoryTotals: Map<String, Double>,
  totalExpense: Double,
  modifier: Modifier = Modifier
) {
  val colors = listOf(
    Color(0xFF10B981), // Food
    Color(0xFF3B82F6), // Transport
    Color(0xFF8B5CF6), // Shopping
    Color(0xFFF59E0B), // Education
    Color(0xFFEF4444), // Bills
    Color(0xFFEC4899), // Healthcare
    Color(0xFF06B6D4), // Entertainment
    Color(0xFF64748B)  // Other
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = PureWhite),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Monthly Spending Breakdown",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
        )
        Text(
          "₹${totalExpense.toInt()}",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = RedExpense)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (categoryTotals.isEmpty() || totalExpense <= 0) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No expense records for this cycle yet.", style = MaterialTheme.typography.bodySmall.copy(color = Slate400))
        }
      } else {
        // Multi-segment horizontal progress bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Slate100)
        ) {
          categoryTotals.entries.forEachIndexed { index, entry ->
            val fraction = (entry.value / totalExpense).toFloat().coerceIn(0f, 1f)
            if (fraction > 0f) {
              Box(
                modifier = Modifier
                  .weight(fraction)
                  .fillMaxHeight()
                  .background(colors[index % colors.size])
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Legend
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categoryTotals.entries.forEachIndexed { index, entry ->
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(colors[index % colors.size])
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "${entry.key}: ₹${entry.value.toInt()} (${(entry.value / totalExpense * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Slate700)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun VerificationBadge(status: String) {
  val (bgColor, textColor, icon) = when (status.uppercase()) {
    "VERIFIED" -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), Icons.Default.Verified)
    "NEEDS_RECHECK" -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Icons.Default.Warning)
    else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.Info)
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.testTag("verification_badge_${status.lowercase()}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Icon(icon, contentDescription = status, tint = textColor, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = status.replace("_", " "),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = textColor,
          fontSize = 10.sp
        )
      )
    }
  }
}

/**
 * OpenStreetMap Canvas Visualizer
 * Renders an interactive map canvas with OpenStreetMap tiles & grid, user coordinates pin, and resource markers.
 */
@Composable
fun OpenStreetMapViewer(
  userLat: Double,
  userLon: Double,
  resources: List<ResourceEntity>,
  onResourceClick: (ResourceEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedMarker by remember { mutableStateOf<ResourceEntity?>(null) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(240.dp)
      .clip(RoundedCornerShape(16.dp))
      .testTag("openstreetmap_canvas"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8ECEF))
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw Map Base Canvas / Grid (Simulating OpenStreetMap street grid)
        drawRect(color = Color(0xFFE5E7EB), size = size)

        // Draw arterial roads & waterways
        drawLine(
          color = Color(0xFF93C5FD),
          start = Offset(0f, h * 0.7f),
          end = Offset(w, h * 0.45f),
          strokeWidth = 14f
        )
        drawLine(
          color = PureWhite,
          start = Offset(w * 0.1f, 0f),
          end = Offset(w * 0.9f, h),
          strokeWidth = 10f
        )
        drawLine(
          color = PureWhite,
          start = Offset(0f, h * 0.35f),
          end = Offset(w, h * 0.35f),
          strokeWidth = 8f
        )
        drawLine(
          color = PureWhite,
          start = Offset(w * 0.5f, 0f),
          end = Offset(w * 0.5f, h),
          strokeWidth = 6f
        )

        // 2. User Location Blue Radar Pulse & Pin (Center)
        val userX = w * 0.5f
        val userY = h * 0.5f

        drawCircle(
          color = Color(0x333B82F6),
          radius = 36f,
          center = Offset(userX, userY)
        )
        drawCircle(
          color = Color(0xFF2563EB),
          radius = 12f,
          center = Offset(userX, userY)
        )
        drawCircle(
          color = PureWhite,
          radius = 5f,
          center = Offset(userX, userY)
        )

        // 3. Resource pins relative to user center
        val latScale = 2400f
        val lonScale = 2400f

        resources.take(12).forEach { res ->
          val dx = ((res.longitude - userLon) * lonScale).toFloat()
          val dy = -((res.latitude - userLat) * latScale).toFloat()
          val pinX = (userX + dx).coerceIn(24f, w - 24f)
          val pinY = (userY + dy).coerceIn(24f, h - 24f)

          val pinColor = if (res.verificationStatus == "VERIFIED") Color(0xFF059669) else Color(0xFFD97706)

          drawCircle(color = pinColor, radius = 10f, center = Offset(pinX, pinY))
          drawCircle(color = PureWhite, radius = 4f, center = Offset(pinX, pinY))
        }
      }

      // Map Watermark & Info badge
      Box(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp)
          .background(Color(0xCC0F172A), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Place, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            "OpenStreetMap Data • GPS Active",
            style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontSize = 9.sp)
          )
        }
      }

      // Floating chips for quick resource pin taps
      Row(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        resources.take(2).forEach { res ->
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = PureWhite.copy(alpha = 0.95f),
            shadowElevation = 2.dp,
            modifier = Modifier.clickable { onResourceClick(res) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (res.verificationStatus == "VERIFIED") EmeraldPrimary else AmberWarning)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                res.name.take(18) + if (res.name.length > 18) "…" else "",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Voice Expense Confirmation Dialog
 * MANDATORY: As per Section 13, user voice is parsed into Candidate and shown with "Did you mean?" + CONFIRM, EDIT, CANCEL.
 */
@Composable
fun VoiceExpenseConfirmationDialog(
  candidate: ParsedExpense,
  onConfirm: () -> Unit,
  onEdit: () -> Unit,
  onCancel: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onCancel,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Mic, contentDescription = null, tint = EmeraldPrimary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(tr("Did you mean?"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          tr("We interpreted your spoken expense as:"),
          style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
        )

        Card(
          colors = CardDefaults.cardColors(containerColor = Slate50),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("${tr("Amount")}:", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
              Text("₹${candidate.amount.toInt()}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RedExpense))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("${tr("Category")}:", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
              Text(tr(candidate.category), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("${tr("Description")}:", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
              Text(candidate.description, style = MaterialTheme.typography.bodyMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("${tr("Date")}:", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
              Text(candidate.date, style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("${tr("Payment Mode")}:", style = MaterialTheme.typography.labelMedium.copy(color = Slate500))
              Text(tr(candidate.paymentMode), style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        modifier = Modifier.testTag("btn_confirm_voice_expense")
      ) {
        Text(tr("Confirm"), fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = onEdit,
          modifier = Modifier.testTag("btn_edit_voice_expense")
        ) {
          Text(tr("Edit"))
        }
        TextButton(
          onClick = onCancel,
          modifier = Modifier.testTag("btn_cancel_voice_expense")
        ) {
          Text(tr("Cancel"), color = Slate600)
        }
      }
    }
  )
}

/**
 * Mobile Bottom Navigation Bar
 */
@Composable
fun FinGuideBottomNavBar(
  currentScreen: AppScreen,
  onNavigate: (AppScreen) -> Unit,
  currentLanguage: String = "en",
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.testTag("bottom_nav_bar"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    windowInsets = WindowInsets.navigationBars
  ) {
    val items = listOf(
      Triple(AppScreen.DASHBOARD, "Home", Icons.Filled.Home),
      Triple(AppScreen.FINANCE, "Finance", Icons.Filled.AccountBalanceWallet),
      Triple(AppScreen.LEARN, "Learn", Icons.Filled.School),
      Triple(AppScreen.RESOURCES, "Resources", Icons.Filled.LocationOn),
      Triple(AppScreen.PROFILE, "Profile", Icons.Filled.Person)
    )

    items.forEach { (screen, label, icon) ->
      val selected = currentScreen == screen ||
        (screen == AppScreen.FINANCE && (currentScreen == AppScreen.ADD_EXPENSE || currentScreen == AppScreen.ADD_INCOME || currentScreen == AppScreen.VOICE_EXPENSE || currentScreen == AppScreen.BUDGET_SAVINGS)) ||
        (screen == AppScreen.LEARN && currentScreen == AppScreen.LEARN_DETAIL) ||
        (screen == AppScreen.RESOURCES && currentScreen == AppScreen.RESOURCE_DETAIL)

      val localizedLabel = LanguageManager.translate(label, currentLanguage)

      NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(screen) },
        icon = { Icon(icon, contentDescription = localizedLabel) },
        label = { Text(localizedLabel, style = MaterialTheme.typography.labelSmall) },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = PureWhite,
          indicatorColor = EmeraldPrimary,
          selectedTextColor = EmeraldPrimary,
          unselectedIconColor = Slate500,
          unselectedTextColor = Slate500
        ),
        modifier = Modifier.testTag("nav_item_${label.lowercase()}")
      )
    }
  }
}

/**
 * Desktop / Tablet Sidebar Navigation Rail
 */
@Composable
fun FinGuideSidebar(
  currentScreen: AppScreen,
  onNavigate: (AppScreen) -> Unit,
  userRole: String,
  onLogout: () -> Unit = {},
  currentLanguage: String = "en",
  modifier: Modifier = Modifier
) {
  NavigationRail(
    modifier = modifier
      .width(190.dp)
      .fillMaxHeight()
      .testTag("desktop_sidebar"),
    containerColor = MaterialTheme.colorScheme.surface,
    header = {
      Column(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.AccountBalanceWallet, contentDescription = "FinGuide", tint = PureWhite)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("FinGuide", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text("Universal Hub", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
      }
    }
  ) {
    val items = if (userRole == "ADMIN") {
      listOf(
        Triple(AppScreen.ADMIN_DASHBOARD, "Admin Dash", Icons.Default.Dashboard),
        Triple(AppScreen.ADMIN_USERS, "Users", Icons.Default.People),
        Triple(AppScreen.ADMIN_RESOURCES, "Resources", Icons.Default.Verified),
        Triple(AppScreen.ADMIN_VERIFICATION, "Verification", Icons.AutoMirrored.Filled.FactCheck),
        Triple(AppScreen.ADMIN_EDUCATION, "Education", Icons.AutoMirrored.Filled.MenuBook),
        Triple(AppScreen.ADMIN_REPORTS, "Reports", Icons.Default.ReportProblem),
        Triple(AppScreen.ADMIN_NOTIFICATIONS, "Broadcast", Icons.Default.Campaign),
        Triple(AppScreen.DASHBOARD, "User View", Icons.Default.SwitchAccount)
      )
    } else {
      listOf(
        Triple(AppScreen.DASHBOARD, "Dashboard", Icons.Default.Home),
        Triple(AppScreen.FINANCE, "Ledger", Icons.AutoMirrored.Filled.ReceiptLong),
        Triple(AppScreen.BUDGET_SAVINGS, "Budgets", Icons.Default.Savings),
        Triple(AppScreen.AI_ASSISTANT, "AI Chat", Icons.Default.SmartToy),
        Triple(AppScreen.LEARN, "Education", Icons.Default.School),
        Triple(AppScreen.RESOURCES, "Locator", Icons.Default.Place),
        Triple(AppScreen.DOCUMENTS, "Documents", Icons.Default.Folder),
        Triple(AppScreen.NOTIFICATIONS, "Alerts", Icons.Default.Notifications),
        Triple(AppScreen.PROFILE, "Settings", Icons.Default.Settings)
      )
    }

    Column(
      modifier = Modifier
        .fillMaxHeight()
        .padding(horizontal = 8.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Box(modifier = Modifier.weight(1f)) {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items.forEach { (screen, label, icon) ->
            val selected = currentScreen == screen
            val localizedLabel = LanguageManager.translate(label, currentLanguage)
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (selected) EmeraldPrimary else Color.Transparent,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(screen) }
                .padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  icon,
                  contentDescription = localizedLabel,
                  tint = if (selected) PureWhite else Slate600,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  localizedLabel,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) PureWhite else Slate700
                  ),
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      Column(
        modifier = Modifier.padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        HorizontalDivider(color = Slate200)
        Button(
          onClick = onLogout,
          colors = ButtonDefaults.buttonColors(containerColor = RedExpense.copy(alpha = 0.12f), contentColor = RedExpense),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_sidebar_logout")
        ) {
          Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(LanguageManager.translate("Sign Out", currentLanguage), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
      }
    }
  }
}

/**
 * Modern FinGuide Menubar Sheet: Full-featured accessible navigation & utilities drawer
 */
@Composable
fun FinGuideMenuBarSheet(
  viewModel: FinGuideViewModel,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val user by viewModel.currentUser.collectAsState()
  val currentScreen by viewModel.currentScreen.collectAsState()
  val syncState by viewModel.syncState.collectAsState()
  val currentLang by viewModel.currentLanguage.collectAsState()
  val forceDesktop by viewModel.forceDesktopLayout.collectAsState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = PureWhite,
    dragHandle = { BottomSheetDefaults.DragHandle(color = Slate300) },
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = Modifier.testTag("menubar_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Menubar Header & Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              "FinGuide Menubar",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
            )
            Text(
              "Universal Financial Navigation",
              style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
            )
          }
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close Menubar", tint = Slate500)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // User Profile Summary Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate50),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user?.name?.firstOrNull()?.uppercase() ?: "U",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = user?.name ?: "Guest User",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = user?.email ?: "guest@finguide.app",
              style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = EmeraldLight
            ) {
              Text(
                text = user?.userType ?: "Student",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = EmeraldDark)
              )
            }
          }

          IconButton(
            onClick = {
              onDismiss()
              viewModel.navigateTo(AppScreen.PROFILE)
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(PureWhite)
              .border(1.dp, Slate200, CircleShape)
          ) {
            Icon(Icons.Default.Settings, contentDescription = "Manage Profile", tint = Slate700, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section: Main Navigation Items
      Text(
        "NAVIGATION MENU",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate500,
          letterSpacing = 1.sp
        )
      )
      Spacer(modifier = Modifier.height(8.dp))

      val menuItems = buildList {
        add(MenubarNavEntry(AppScreen.DASHBOARD, "Dashboard", "Overview & Financial Health", Icons.Default.Dashboard))
        add(MenubarNavEntry(AppScreen.FINANCE, "Financial Ledger", "Expenses, Income & Voice Log", Icons.AutoMirrored.Filled.ReceiptLong))
        add(MenubarNavEntry(AppScreen.BUDGET_SAVINGS, "Budget & Savings", "Monthly Limits & Goals", Icons.Default.Savings))
        add(MenubarNavEntry(AppScreen.AI_ASSISTANT, "AI Advisor (Gemini)", "Smart Guidance & Analysis", Icons.Default.SmartToy))
        add(MenubarNavEntry(AppScreen.LEARN, "Financial Education", "Curated Lessons & Quizzes", Icons.Default.School))
        add(MenubarNavEntry(AppScreen.RESOURCES, "Advisory Directory", "SEBI, RBI & Nearest Advisors", Icons.Default.Place))
        add(MenubarNavEntry(AppScreen.DOCUMENTS, "Document Vault", "Offline Secure Records & KYC", Icons.Default.Folder))
        add(MenubarNavEntry(AppScreen.NOTIFICATIONS, "Notifications", "Reminders & Alerts", Icons.Default.Notifications))
        add(MenubarNavEntry(AppScreen.PROFILE, "Profile & Address", "Residential Location & Role", Icons.Default.Person))
        if (user?.role == "ADMIN") {
          add(MenubarNavEntry(AppScreen.ADMIN_DASHBOARD, "Admin Control Portal", "Verify Resources & Broadcast", Icons.Default.AdminPanelSettings))
        }
      }

      menuItems.forEach { item ->
        val isSelected = currentScreen == item.screen
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isSelected) EmeraldLight else Color.Transparent,
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              onDismiss()
              viewModel.navigateTo(item.screen)
            }
            .padding(vertical = 2.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) EmeraldPrimary else Slate100),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                item.icon,
                contentDescription = null,
                tint = if (isSelected) PureWhite else Slate700,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = LanguageManager.translate(item.title, currentLang),
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                  color = if (isSelected) EmeraldDark else Slate900
                )
              )
              Text(
                text = LanguageManager.translate(item.subtitle, currentLang),
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                maxLines = 1
              )
            }

            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            } else {
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Slate300, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section: Quick Utilities
      Text(
        LanguageManager.translate("QUICK UTILITIES", currentLang),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate500,
          letterSpacing = 1.sp
        )
      )
      Spacer(modifier = Modifier.height(8.dp))

      // Cloud Sync Row
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.triggerFirestoreSync() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Sync, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(LanguageManager.translate("Sync Room to Firestore", currentLang), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate900))
              Text(
                when (syncState) {
                  is com.example.data.sync.SyncState.Syncing -> "Sync in progress..."
                  is com.example.data.sync.SyncState.Success -> "Cloud ledger updated"
                  else -> "Tap to backup & sync records"
                },
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
              )
            }
          }
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Language Switcher Row
      var showLangMenuInSheet by remember { mutableStateOf(false) }
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showLangMenuInSheet = true }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Translate, contentDescription = null, tint = Slate700, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(LanguageManager.translate("Language Preferences", currentLang), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate900))
              Text(
                LanguageManager.supportedLanguages.find { it.code == currentLang }?.let { "${it.englishName} (${it.nativeName})" } ?: "English",
                style = MaterialTheme.typography.bodySmall.copy(color = EmeraldDark, fontWeight = FontWeight.Medium)
              )
            }
          }
          Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate400)
        }
      }

      if (showLangMenuInSheet) {
        AlertDialog(
          onDismissRequest = { showLangMenuInSheet = false },
          title = { Text("Select Language", color = Slate900, fontWeight = FontWeight.Bold) },
          text = {
            Column {
              LanguageManager.supportedLanguages.forEach { lang ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      viewModel.setLanguage(lang.code)
                      showLangMenuInSheet = false
                    }
                    .padding(vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("${lang.englishName} (${lang.nativeName})", color = Slate900, style = MaterialTheme.typography.bodyMedium)
                  if (lang.code == currentLang) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                  }
                }
              }
            }
          },
          confirmButton = {
            TextButton(onClick = { showLangMenuInSheet = false }) { Text("Close") }
          }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Toggle Layout View Mode
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.toggleLayoutMode() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (forceDesktop == true) Icons.Default.Laptop else Icons.Default.Smartphone,
              contentDescription = null,
              tint = Slate700,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(LanguageManager.translate("Display Mode", currentLang), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Slate900))
              Text(
                if (forceDesktop == true) "Desktop / Tablet Dual-Pane" else "Standard Mobile Handheld",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
              )
            }
          }
          Text("Toggle", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.Bold))
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Log Out Action Button
      Button(
        onClick = {
          onDismiss()
          viewModel.requestLogout()
        },
        colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("btn_menubar_logout")
      ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = PureWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text(LanguageManager.translate("Sign Out", currentLang), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = PureWhite))
      }
    }
  }
}

private data class MenubarNavEntry(
  val screen: AppScreen,
  val title: String,
  val subtitle: String,
  val icon: ImageVector
)

/**
 * Logout Confirmation Alert Dialog
 */
@Composable
fun LogoutConfirmationDialog(
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(RedExpense.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.AutoMirrored.Filled.Logout,
          contentDescription = null,
          tint = RedExpense,
          modifier = Modifier.size(26.dp)
        )
      }
    },
    title = {
      Text(
        text = "Log Out of FinGuide?",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate900
        )
      )
    },
    text = {
      Text(
        text = "Are you sure you want to end your current session? Your offline ledger, budgets, and KYC documents are safely encrypted locally.",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("btn_confirm_logout")
      ) {
        Text("Sign Out", fontWeight = FontWeight.Bold, color = PureWhite)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Cancel", color = Slate700)
      }
    },
    containerColor = PureWhite,
    shape = RoundedCornerShape(16.dp)
  )
}

/**
 * Reassuring Logout Progress Dialog showing steps and progress bar
 */
@Composable
fun LogoutProgressDialog(
  progress: Float,
  statusMessage: String
) {
  Dialog(
    onDismissRequest = { /* Prevent dismissal during session teardown */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = PureWhite),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .testTag("dialog_logout_progress")
    ) {
      Column(
        modifier = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(EmeraldLight),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(56.dp),
            color = EmeraldPrimary,
            trackColor = Slate200,
            strokeWidth = 4.dp
          )
          Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Signing Out...",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Slate900
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = statusMessage.ifBlank { "Securing your financial session..." },
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Slate600,
            textAlign = TextAlign.Center
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = EmeraldPrimary,
          trackColor = Slate100
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Local Ledger Encrypted",
            style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
          )
          Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          )
        }
      }
    }
  }
}
