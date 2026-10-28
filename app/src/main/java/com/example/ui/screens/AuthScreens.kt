@file:OptIn(ExperimentalLayoutApi::class)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserType
import com.example.i18n.LanguageManager
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.fintechTextFieldColors
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: FinGuideViewModel) {
  LaunchedEffect(Unit) {
    delay(1800)
    if (viewModel.currentUser.value != null) {
      viewModel.navigateTo(AppScreen.DASHBOARD)
    } else {
      viewModel.navigateTo(AppScreen.ONBOARDING)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(listOf(EmeraldDark, Slate900)))
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.padding(24.dp)
    ) {
      Box(
        modifier = Modifier
          .size(88.dp)
          .shadow(16.dp, CircleShape)
          .clip(CircleShape)
          .background(Brush.linearGradient(listOf(EmeraldPrimary, GoldAccent))),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.AccountBalanceWallet,
          contentDescription = "Logo",
          tint = PureWhite,
          modifier = Modifier.size(46.dp)
        )
      }

      Text(
        text = "FinGuide",
        style = MaterialTheme.typography.headlineLarge.copy(
          fontWeight = FontWeight.ExtraBold,
          color = PureWhite,
          letterSpacing = (-0.5).sp
        )
      )

      Text(
        text = "Manage Money. Discover Resources. Build a Better Future.",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Slate300,
          textAlign = TextAlign.Center
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      CircularProgressIndicator(
        color = EmeraldLight,
        strokeWidth = 3.dp,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}

@Composable
fun OnboardingScreen(viewModel: FinGuideViewModel) {
  var step by remember { mutableStateOf(0) }

  val slides = listOf(
    Triple(
      "Manage Your Money",
      "Track daily income, expenses, category budgets, and savings goals effortlessly with smart analytics.",
      Icons.Default.Savings
    ),
    Triple(
      "Understand Your Finances",
      "Get AI-assisted insights, spending explanations, and 14 financial literacy modules vetted by experts.",
      Icons.Default.Psychology
    ),
    Triple(
      "Find Verified Resources",
      "Locate officially registered banks, SEBI advisers, IBBI valuers, BIS jewellers, and TNREGINET land offices near you.",
      Icons.Default.Verified
    )
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(24.dp)
      .testTag("onboarding_screen"),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Skip
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
        modifier = Modifier.testTag("btn_onboarding_skip")
      ) {
        Text("Skip", color = Slate600, fontWeight = FontWeight.SemiBold)
      }
    }

    // Center Illustration & Content
    val currentSlide = slides[step]
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(120.dp)
          .clip(CircleShape)
          .background(EmeraldSurface),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          currentSlide.third,
          contentDescription = null,
          tint = EmeraldPrimary,
          modifier = Modifier.size(60.dp)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = currentSlide.first,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate900,
          textAlign = TextAlign.Center
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = currentSlide.second,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Slate600,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        ),
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Dots indicator
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        slides.indices.forEach { index ->
          Box(
            modifier = Modifier
              .width(if (index == step) 28.dp else 8.dp)
              .height(8.dp)
              .clip(CircleShape)
              .background(if (index == step) EmeraldPrimary else Slate300)
          )
        }
      }
    }

    // Bottom Navigation Buttons
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Button(
        onClick = {
          if (step < slides.size - 1) {
            step++
          } else {
            viewModel.navigateTo(AppScreen.LOGIN)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("btn_onboarding_next"),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(14.dp)
      ) {
        Text(
          if (step == slides.size - 1) "Get Started" else "Next",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp
        )
      }
    }
  }
}

@Composable
fun LoginScreen(viewModel: FinGuideViewModel) {
  var email by remember { mutableStateOf("student@finguide.com") }
  var password by remember { mutableStateOf("demo123") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(24.dp)
      .testTag("login_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Logo & Header
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDark))),
      contentAlignment = Alignment.Center
    ) {
      Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PureWhite, modifier = Modifier.size(30.dp))
    }

    Text(
      "Welcome to FinGuide",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
    )
    Text(
      "Sign in to manage your finances, track budgets, and locate verified authorities.",
      style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
    )

    if (errorMessage != null) {
      Surface(
        color = Color(0xFFFEE2E2),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          errorMessage!!,
          color = RedExpense,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          modifier = Modifier.padding(12.dp)
        )
      }
    }

    // Input fields (black text on light background)
    OutlinedTextField(
      value = email,
      onValueChange = { email = it; errorMessage = null },
      label = { Text("Email Address") },
      leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_login_email"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    OutlinedTextField(
      value = password,
      onValueChange = { password = it; errorMessage = null },
      label = { Text("Password") },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      visualTransformation = PasswordVisualTransformation(),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_login_password"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    Button(
      onClick = {
        isLoading = true
        viewModel.login(
          email = email,
          pass = password,
          onSuccess = { isLoading = false },
          onError = { err ->
            isLoading = false
            errorMessage = err
          }
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("btn_submit_login"),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
      shape = RoundedCornerShape(14.dp),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
      } else {
        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    // Quick fill demo helper
    Card(
      colors = CardDefaults.cardColors(containerColor = Slate100),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Quick Fill Demo Accounts:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Slate700))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = {
              email = "student@finguide.com"
              password = "demo123"
            },
            modifier = Modifier.weight(1f).testTag("btn_quick_user")
          ) {
            Text("User Demo", fontSize = 12.sp)
          }
          OutlinedButton(
            onClick = {
              email = "admin@finguide.com"
              password = "admin123"
            },
            modifier = Modifier.weight(1f).testTag("btn_quick_admin")
          ) {
            Text("Admin Demo", fontSize = 12.sp)
          }
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium.copy(color = Slate600))
      TextButton(
        onClick = { viewModel.navigateTo(AppScreen.REGISTER) },
        modifier = Modifier.testTag("btn_goto_register")
      ) {
        Text("Create Account", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
      }
    }

    HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))

    // Switch to Dedicated Admin Portal Login
    OutlinedButton(
      onClick = { viewModel.navigateTo(AppScreen.ADMIN_LOGIN) },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("btn_goto_admin_login"),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1D4ED8))
    ) {
      Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Administrator Secure Portal Login", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
  }
}

@Composable
fun AdminLoginScreen(viewModel: FinGuideViewModel) {
  var email by remember { mutableStateOf("admin@finguide.com") }
  var password by remember { mutableStateOf("admin123") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(24.dp)
      .testTag("admin_login_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { viewModel.navigateTo(AppScreen.LOGIN) }) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate700)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text("Admin Portal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate800))
    }

    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF1D4ED8)),
      contentAlignment = Alignment.Center
    ) {
      Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PureWhite, modifier = Modifier.size(32.dp))
    }

    Text(
      "Admin Portal Sign In",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Slate900)
    )
    Text(
      "Restricted access for official authority registrars and FinGuide administrators only.",
      style = MaterialTheme.typography.bodyMedium.copy(color = Slate600)
    )

    if (errorMessage != null) {
      Surface(
        color = Color(0xFFFEE2E2),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          errorMessage!!,
          color = RedExpense,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          modifier = Modifier.padding(12.dp)
        )
      }
    }

    OutlinedTextField(
      value = email,
      onValueChange = { email = it; errorMessage = null },
      label = { Text("Admin Email Address") },
      leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_admin_email"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    OutlinedTextField(
      value = password,
      onValueChange = { password = it; errorMessage = null },
      label = { Text("Admin Password") },
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      visualTransformation = PasswordVisualTransformation(),
      modifier = Modifier.fillMaxWidth().testTag("input_admin_password"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    Button(
      onClick = {
        isLoading = true
        viewModel.adminLogin(
          email = email,
          pass = password,
          onSuccess = { isLoading = false },
          onError = { err ->
            isLoading = false
            errorMessage = err
          }
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("btn_submit_admin_login"),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
      shape = RoundedCornerShape(14.dp),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
      } else {
        Text("Admin Secure Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Demo Admin Credentials:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF)))
        Text("Email: admin@finguide.com\nPassword: admin123", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF3B82F6)))
      }
    }

    TextButton(
      onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Back to User Login", color = Slate700, fontWeight = FontWeight.SemiBold)
    }
  }
}

@Composable
fun RegisterScreen(viewModel: FinGuideViewModel) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var addressLine by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("") }
  var state by remember { mutableStateOf("Tamil Nadu") }
  var postalCode by remember { mutableStateOf("") }
  var selectedUserType by remember { mutableStateOf(UserType.STUDENT.name) }
  var selectedLanguage by remember { mutableStateOf("en") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate50)
      .statusBarsPadding()
      .navigationBarsPadding()
      .verticalScroll(rememberScrollState())
      .padding(24.dp)
      .testTag("register_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      "Create Your FinGuide Account",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Slate900)
    )
    Text(
      "Register with your address details and preferred Indian language for localized nearby authorities.",
      style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
    )

    if (errorMessage != null) {
      Surface(
        color = Color(0xFFFEE2E2),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(errorMessage!!, color = RedExpense, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
      }
    }

    // Full Name
    OutlinedTextField(
      value = name,
      onValueChange = { name = it },
      label = { Text("Full Name *") },
      leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_name"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    // Email Address
    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("Email Address *") },
      leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    // Password
    OutlinedTextField(
      value = password,
      onValueChange = { password = it },
      label = { Text("Password *") },
      visualTransformation = PasswordVisualTransformation(),
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_password"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    OutlinedTextField(
      value = confirmPassword,
      onValueChange = { confirmPassword = it },
      label = { Text("Confirm Password *") },
      visualTransformation = PasswordVisualTransformation(),
      leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_confirm_password"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    // Residential Address Section (Required by prompt)
    Text("Residential Address *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate800))

    OutlinedTextField(
      value = addressLine,
      onValueChange = { addressLine = it },
      label = { Text("Address Line / Street *") },
      leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Slate500) },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_address"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      OutlinedTextField(
        value = city,
        onValueChange = { city = it },
        label = { Text("City *") },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier.weight(1f).testTag("input_reg_city"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )
      OutlinedTextField(
        value = postalCode,
        onValueChange = { postalCode = it },
        label = { Text("PIN Code") },
        colors = fintechTextFieldColors(),
        textStyle = LocalTextStyle.current.copy(color = Slate900),
        modifier = Modifier.weight(1f).testTag("input_reg_pincode"),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
      )
    }

    OutlinedTextField(
      value = state,
      onValueChange = { state = it },
      label = { Text("State") },
      colors = fintechTextFieldColors(),
      textStyle = LocalTextStyle.current.copy(color = Slate900),
      modifier = Modifier.fillMaxWidth().testTag("input_reg_state"),
      shape = RoundedCornerShape(12.dp),
      singleLine = true
    )

    // User Role Selection
    Text("User Role / Profession *", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate800))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      UserType.entries.forEach { ut ->
        FilterChip(
          selected = selectedUserType == ut.name,
          onClick = { selectedUserType = ut.name },
          label = { Text(ut.label) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EmeraldPrimary,
            selectedLabelColor = PureWhite
          )
        )
      }
    }

    // Preferred Language Selection (15 Indian Languages)
    var langMenuExpanded by remember { mutableStateOf(false) }
    Text("Preferred Language (15 Indian Languages):", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate800))
    Box {
      OutlinedButton(
        onClick = { langMenuExpanded = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      ) {
        val current = LanguageManager.supportedLanguages.find { it.code == selectedLanguage }
        Text("${current?.englishName} (${current?.nativeName})", modifier = Modifier.weight(1f), textAlign = TextAlign.Start, color = Slate900)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Slate600)
      }

      DropdownMenu(
        expanded = langMenuExpanded,
        onDismissRequest = { langMenuExpanded = false }
      ) {
        LanguageManager.supportedLanguages.forEach { lang ->
          DropdownMenuItem(
            text = { Text("${lang.englishName} (${lang.nativeName})") },
            onClick = {
              selectedLanguage = lang.code
              langMenuExpanded = false
            }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Button(
      onClick = {
        if (password != confirmPassword) {
          errorMessage = "Passwords do not match"
          return@Button
        }
        if (addressLine.isBlank() || city.isBlank()) {
          errorMessage = "Address line and city cannot be empty"
          return@Button
        }
        isLoading = true
        viewModel.register(
          name = name,
          email = email,
          pass = password,
          userType = selectedUserType,
          lang = selectedLanguage,
          address = addressLine,
          city = city,
          state = state,
          postalCode = postalCode,
          onSuccess = { isLoading = false },
          onError = { err ->
            isLoading = false
            errorMessage = err
          }
        )
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("btn_submit_register"),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
      shape = RoundedCornerShape(14.dp),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
      } else {
        Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Already have an account?", style = MaterialTheme.typography.bodyMedium.copy(color = Slate600))
      TextButton(onClick = { viewModel.navigateTo(AppScreen.LOGIN) }) {
        Text("Sign In", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
      }
    }
  }
}
