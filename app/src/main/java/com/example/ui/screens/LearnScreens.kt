package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArticleEntity
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideHeader
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*

@Composable
fun LearnScreen(viewModel: FinGuideViewModel) {
  val articles by viewModel.publishedArticles.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("All") }

  val categories = listOf(
    "All", "Budgeting", "Saving", "Emergency Fund", "Banking", "Loans", "Interest",
    "Inflation", "Mutual Funds", "SIP", "FD", "Gold", "Government Bonds", "Insurance", "Financial Safety"
  )

  val filteredArticles = remember(articles, searchQuery, selectedCategory) {
    articles.filter { art ->
      val catMatch = selectedCategory == "All" || art.category.equals(selectedCategory, ignoreCase = true)
      val searchMatch = searchQuery.isBlank() ||
        art.title.contains(searchQuery, ignoreCase = true) ||
        art.summary.contains(searchQuery, ignoreCase = true) ||
        art.category.contains(searchQuery, ignoreCase = true)
      catMatch && searchMatch
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("learn_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "Financial Education",
      subtitle = "14 Practical Literacy Modules"
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search topics, e.g. SIP, Inflation, Gold...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("input_search_education"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )

      // Category Chips Horizontal Scroll
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        items(categories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EmeraldPrimary,
              selectedLabelColor = PureWhite
            )
          )
        }
      }
    }

    // Article Cards List
    if (filteredArticles.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("No educational guides matching your search.", style = MaterialTheme.typography.bodyMedium.copy(color = Slate400))
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
      ) {
        items(filteredArticles, key = { it.id }) { article ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.selectArticle(article) }
              .testTag("article_card_${article.id}"),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder)))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  color = EmeraldSurface,
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    article.category,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldDark),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Text(
                  article.sourceAuthority,
                  style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                )
              }

              Text(
                article.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
              )

              Text(
                article.summary,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate600, lineHeight = 18.sp),
                maxLines = 2
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Read Guide", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun LearnDetailScreen(viewModel: FinGuideViewModel) {
  val article by viewModel.selectedArticle.collectAsState()

  if (article == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No article selected.")
    }
    return
  }

  val art = article!!
  val keyPoints = art.keyPointsJson.split("|").filter { it.isNotBlank() }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .verticalScroll(rememberScrollState())
      .testTag("learn_detail_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = art.category,
      subtitle = art.sourceAuthority,
      showBack = true,
      onBack = { viewModel.navigateTo(AppScreen.LEARN) }
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Title
      Text(
        art.title,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
      )

      // Summary Card
      Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
          Icon(Icons.Default.Bookmark, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(art.summary, style = MaterialTheme.typography.bodyMedium.copy(color = EmeraldDark, fontWeight = FontWeight.Medium))
        }
      }

      // Detailed Explanation
      Text("In-Depth Explanation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
      Text(
        art.explanation,
        style = MaterialTheme.typography.bodyLarge.copy(color = Slate800, lineHeight = 24.sp)
      )

      // Real-World Example
      Card(
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Real-World Example", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
          }
          Text(
            art.example,
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate700, lineHeight = 20.sp)
          )
        }
      }

      // Key Takeaways Checklist
      if (keyPoints.isNotEmpty()) {
        Text("Key Takeaways", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate900))
        Card(
          colors = CardDefaults.cardColors(containerColor = PureWhite),
          border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardBorder, CardBorder))),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            keyPoints.forEach { point ->
              Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(point.trim(), style = MaterialTheme.typography.bodyMedium.copy(color = Slate800))
              }
            }
          }
        }
      }

      // Authority Source Badge
      Card(
        colors = CardDefaults.cardColors(containerColor = Slate100),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Source Reference", style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
            Text(art.sourceAuthority, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Slate800))
          }
          Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldPrimary)
        }
      }
    }
  }
}
