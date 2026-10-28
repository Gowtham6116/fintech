package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.ChatMessageEntity
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideHeader
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(viewModel: FinGuideViewModel) {
  val chatHistory by viewModel.chatHistory.collectAsState()
  val isThinking by viewModel.isAiThinking.collectAsState()
  var currentInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  val suggestedPrompts = listOf(
    "Why did my expenses increase this month?",
    "How can I reduce my expenses?",
    "How much did I spend this month?",
    "What is my highest expense category?",
    "Explain compound interest.",
    "How can I build an emergency fund?"
  )

  LaunchedEffect(chatHistory.size, isThinking) {
    if (chatHistory.isNotEmpty()) {
      coroutineScope.launch {
        listState.animateScrollToItem(chatHistory.size - 1)
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .testTag("ai_assistant_screen")
  ) {
    FinGuideHeader(
      viewModel = viewModel,
      title = "FinGuide AI Assistant",
      subtitle = "Powered by Gemini Intelligence"
    )

    // AI Safety & Capability Disclaimer
    Surface(
      color = EmeraldSurface,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          "Educational guidance & spending analysis. Not registered SEBI investment advice.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = EmeraldDark)
        )
      }
    }

    // Chat History Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      if (chatHistory.isEmpty()) {
        item {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(EmeraldSurface),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.SmartToy, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(34.dp))
            }
            Text("Ask FinGuide AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
              "I can analyze your spending patterns, explain complex banking terms, and guide your savings habits.",
              style = MaterialTheme.typography.bodySmall.copy(color = Slate500, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
              modifier = Modifier.padding(horizontal = 24.dp)
            )
          }
        }
      }

      items(chatHistory, key = { it.id }) { msg ->
        val isUser = msg.sender == "USER"
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
          if (!isUser) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          Surface(
            color = if (isUser) EmeraldPrimary else PureWhite,
            shape = RoundedCornerShape(
              topStart = 16.dp,
              topEnd = 16.dp,
              bottomStart = if (isUser) 16.dp else 4.dp,
              bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
          ) {
            Text(
              text = msg.message,
              color = if (isUser) PureWhite else Slate900,
              style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
              modifier = Modifier.padding(12.dp)
            )
          }
        }
      }

      if (isThinking) {
        item {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              color = PureWhite,
              shape = RoundedCornerShape(12.dp),
              shadowElevation = 1.dp
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing your financial context...", style = MaterialTheme.typography.bodySmall.copy(color = Slate600))
              }
            }
          }
        }
      }
    }

    // Suggested Prompt Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .background(Slate100)
        .padding(vertical = 8.dp, horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(suggestedPrompts) { prompt ->
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = PureWhite,
          shadowElevation = 1.dp,
          modifier = Modifier.clickable {
            viewModel.sendAiMessage(prompt)
          }
        ) {
          Text(
            prompt,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Slate800, fontWeight = FontWeight.Medium)
          )
        }
      }
    }

    // Input Bar
    Surface(
      color = PureWhite,
      shadowElevation = 8.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = currentInput,
          onValueChange = { currentInput = it },
          placeholder = { Text("Ask about budgeting, interest, savings...") },
          colors = fintechTextFieldColors(),
          textStyle = LocalTextStyle.current.copy(color = Slate900),
          modifier = Modifier
            .weight(1f)
            .testTag("input_ai_chat"),
          shape = RoundedCornerShape(24.dp),
          maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (currentInput.isNotBlank()) {
              val text = currentInput
              currentInput = ""
              viewModel.sendAiMessage(text)
            }
          },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(EmeraldPrimary)
            .testTag("btn_send_ai_message")
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = PureWhite, modifier = Modifier.size(20.dp))
        }
      }
    }
  }
}
