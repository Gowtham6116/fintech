package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResourceEntity
import com.example.i18n.LanguageManager
import com.example.location.LocationService
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideHeader
import com.example.ui.components.OpenStreetMapViewer
import com.example.ui.components.VerificationBadge
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceLocatorScreen(viewModel: FinGuideViewModel) {
  val resources by viewModel.activeResources.collectAsState()
  val userCoords by viewModel.userCoordinates.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val userMentionedCity by viewModel.userMentionedCity.collectAsState()
  val currentLanguage by viewModel.currentLanguage.collectAsState()

  val preferredLocation = remember(userMentionedCity, currentUser) {
    if (userMentionedCity.isNotBlank()) userMentionedCity else currentUser?.city ?: ""
  }

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }
  var selectedStatus by remember { mutableStateOf("All") }
  var showMap by remember { mutableStateOf(true) }
  var showLocationPicker by remember { mutableStateOf(false) }
  var manualLocationInput by remember { mutableStateOf("") }

  val categories = listOf(
    "All", "Banks", "Investment Advisers", "Registered Valuers", "Jewellers",
    "Insurance", "Land Registration Offices", "Property Services"
  )

  val filteredResources = remember(resources, searchQuery, selectedCategory, selectedStatus, userCoords, preferredLocation) {
    resources.filter { res ->
      val catMatch = selectedCategory == "All" || res.category.equals(selectedCategory, ignoreCase = true)
      val statusMatch = selectedStatus == "All" || res.verificationStatus.equals(selectedStatus, ignoreCase = true)
      val searchMatch = searchQuery.isBlank() ||
        res.name.contains(searchQuery, ignoreCase = true) ||
        res.authority.contains(searchQuery, ignoreCase = true) ||
        res.registrationNumber.contains(searchQuery, ignoreCase = true) ||
        res.city.contains(searchQuery, ignoreCase = true)
      catMatch && statusMatch && searchMatch
    }.sortedWith(
      compareByDescending<ResourceEntity> { res ->
        if (preferredLocation.isNotBlank() && (res.city.contains(preferredLocation, ignoreCase = true) || preferredLocation.contains(res.city, ignoreCase = true))) 1 else 0
      }.thenBy { res ->
        LocationService.calculateDistanceKm(userCoords.latitude, userCoords.longitude, res.latitude, res.longitude)
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("resource_locator_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Verified Authorities",
      subtitle = "SEBI • IBBI • BIS • TNREGINET • RBI"
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // User Mentioned Location Preference Banner
      Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.Place, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                LanguageManager.translate("Preferred Location", currentLanguage),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate600, fontWeight = FontWeight.SemiBold)
              )
              Text(
                if (preferredLocation.isNotBlank()) preferredLocation else userCoords.locationName,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
              onClick = { showLocationPicker = true },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(LanguageManager.translate("Change", currentLanguage), fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
            TextButton(
              onClick = { viewModel.refreshLocation() },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("GPS", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Location Change Dialog
      if (showLocationPicker) {
        AlertDialog(
          onDismissRequest = { showLocationPicker = false },
          title = {
            Text(
              LanguageManager.translate("Set Preferred Location", currentLanguage),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          },
          text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                "Filter and prioritize verified resources near your chosen city or district:",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
              )
              OutlinedTextField(
                value = manualLocationInput,
                onValueChange = { manualLocationInput = it },
                placeholder = { Text("Enter city e.g. Chennai, Madurai...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
              Text("Quick Pick:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate500))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf("Chennai", "Coimbatore", "Madurai", "Salem").forEach { city ->
                  SuggestionChip(
                    onClick = {
                      viewModel.setUserMentionedLocation(city)
                      showLocationPicker = false
                    },
                    label = { Text(city, fontSize = 11.sp) }
                  )
                }
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                listOf("Bangalore", "Mumbai", "Delhi", "Trichy").forEach { city ->
                  SuggestionChip(
                    onClick = {
                      viewModel.setUserMentionedLocation(city)
                      showLocationPicker = false
                    },
                    label = { Text(city, fontSize = 11.sp) }
                  )
                }
              }
            }
          },
          confirmButton = {
            Button(
              onClick = {
                if (manualLocationInput.isNotBlank()) {
                  viewModel.setUserMentionedLocation(manualLocationInput)
                }
                showLocationPicker = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
              Text("Apply")
            }
          },
          dismissButton = {
            TextButton(onClick = { showLocationPicker = false }) {
              Text("Cancel")
            }
          }
        )
      }

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by name, authority (e.g. SEBI, RBI), reg #...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("input_search_resources"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Category Horizontal Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
      ) {
        items(categories) { cat ->
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

      // Toggle Map / List View
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "${filteredResources.size} nearby authorities found",
          style = MaterialTheme.typography.labelSmall.copy(color = Slate600, fontWeight = FontWeight.Medium)
        )
        TextButton(
          onClick = { showMap = !showMap },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
        ) {
          Icon(if (showMap) Icons.Default.List else Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (showMap) "Hide Map" else "Show Map", fontSize = 11.sp)
        }
      }
    }

    // Interactive OpenStreetMap Canvas
    if (showMap) {
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        OpenStreetMapViewer(
          userLat = userCoords.latitude,
          userLon = userCoords.longitude,
          resources = filteredResources,
          onResourceClick = { viewModel.selectResource(it) }
        )
      }
    }

    // List of Verified Resources
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp)
    ) {
      items(filteredResources, key = { it.id }) { res ->
        val distanceKm = LocationService.calculateDistanceKm(
          userCoords.latitude, userCoords.longitude, res.latitude, res.longitude
        )
        val isPreferredCity = preferredLocation.isNotBlank() && (
          res.city.contains(preferredLocation, ignoreCase = true) ||
          preferredLocation.contains(res.city, ignoreCase = true)
        )

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.selectResource(res) }
            .testTag("resource_card_${res.id}"),
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isPreferredCity) Brush.linearGradient(listOf(EmeraldPrimary, EmeraldPrimary))
                    else Brush.linearGradient(listOf(CardBorder, CardBorder))
          )
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              VerificationBadge(status = res.verificationStatus)
              Text(
                "${String.format("%.1f", distanceKm)} km away",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark)
              )
            }

            if (isPreferredCity) {
              Surface(color = EmeraldSurface, shape = RoundedCornerShape(4.dp)) {
                Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Place, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    LanguageManager.translate("In Your Preferred Location", currentLanguage) + " (${res.city})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark, fontSize = 11.sp)
                  )
                }
              }
            }

            Text(
              res.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(color = Slate100, shape = RoundedCornerShape(4.dp)) {
                Text(
                  "Authority: ${res.authority}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                "Reg: ${res.registrationNumber}",
                style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 11.sp)
              )
            }

            Text(
              "${res.address}, ${res.city}",
              style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
              maxLines = 1
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceDetailScreen(viewModel: FinGuideViewModel) {
  val resource by viewModel.selectedResource.collectAsState()
  val userCoords by viewModel.userCoordinates.collectAsState()
  val context = LocalContext.current
  var showReportDialog by remember { mutableStateOf(false) }

  if (resource == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No resource selected.")
    }
    return
  }

  val res = resource!!
  val distanceKm = LocationService.calculateDistanceKm(
    userCoords.latitude, userCoords.longitude, res.latitude, res.longitude
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("resource_detail_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = res.name,
      subtitle = res.category,
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.RESOURCES) }
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Status Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        VerificationBadge(status = res.verificationStatus)
        Text(
          "${String.format("%.1f", distanceKm)} km from your current GPS",
          style = MaterialTheme.typography.labelMedium.copy(color = EmeraldDark, fontWeight = FontWeight.Bold)
        )
      }

      Text(
        res.name,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
      )

      // Official Authority Card
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Official Accreditation", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
          HorizontalDivider(color = Slate100)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Regulating Authority:", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
            Text(res.authority, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Registration Number:", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
            Text(res.registrationNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Last Verified Date:", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
            Text(res.lastVerifiedAt, style = MaterialTheme.typography.bodySmall.copy(color = Slate700))
          }
        }
      }

      // Location & Contact Card
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Address & Coordinates", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Slate900))
          HorizontalDivider(color = Slate100)
          Text("${res.address}, ${res.city}, ${res.state}", style = MaterialTheme.typography.bodyMedium.copy(color = Slate800))
          Text("Phone: ${res.phone}", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
          Text("Coordinates: ${res.latitude}, ${res.longitude}", style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp))
        }
      }

      // Action Buttons
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${res.phone}"))
            context.startActivity(intent)
          },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call")
        }

        OutlinedButton(
          onClick = {
            val gmmIntentUri = Uri.parse("geo:${res.latitude},${res.longitude}?q=${Uri.encode(res.name)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            context.startActivity(mapIntent)
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Directions")
        }
      }

      // View Official Registry Link
      if (res.sourceUrl.isNotBlank()) {
        OutlinedButton(
          onClick = {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(res.sourceUrl))
            context.startActivity(browserIntent)
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.OpenInBrowser, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Verify in Official Government Portal")
        }
      }

      // Report Incorrect Information Button
      TextButton(
        onClick = { showReportDialog = true },
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = RedExpense, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Report Incorrect or Outdated Information", color = RedExpense)
      }
    }
  }

  // Report Dialog
  if (showReportDialog) {
    var reportReason by remember { mutableStateOf("Address changed") }
    var reportDesc by remember { mutableStateOf("") }
    var reportSubmitted by remember { mutableStateOf(false) }

    val reasons = listOf("Address changed", "Phone unreachable", "Registration expired", "Not verified in registry", "Other")

    AlertDialog(
      onDismissRequest = { showReportDialog = false },
      title = { Text("Report Resource") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Reason for reporting:", style = MaterialTheme.typography.labelMedium)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            reasons.forEach { r ->
              FilterChip(
                selected = reportReason == r,
                onClick = { reportReason = r },
                label = { Text(r, fontSize = 11.sp) }
              )
            }
          }
          OutlinedTextField(
            value = reportDesc,
            onValueChange = { reportDesc = it },
            label = { Text("Details / Proof") },
            colors = fintechTextFieldColors(),
            textStyle = LocalTextStyle.current.copy(color = Slate900),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.reportResource(reportReason, reportDesc) {
              showReportDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
        ) {
          Text("Submit Report")
        }
      },
      dismissButton = {
        TextButton(onClick = { showReportDialog = false }) { Text("Cancel") }
      }
    )
  }
}
