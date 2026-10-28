package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.i18n.LocalAppLanguage
import com.example.ui.AppScreen
import com.example.ui.FinGuideViewModel
import com.example.ui.components.FinGuideBottomNavBar
import com.example.ui.components.FinGuideMenuBarSheet
import com.example.ui.components.FinGuideSidebar
import com.example.ui.components.LogoutConfirmationDialog
import com.example.ui.components.LogoutProgressDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate50

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FinGuideApp()
      }
    }
  }
}

@Composable
fun FinGuideApp(viewModel: FinGuideViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val forceDesktop by viewModel.forceDesktopLayout.collectAsState()
  val isMenuBarOpen by viewModel.isMenuBarOpen.collectAsState()
  val isLoggingOut by viewModel.isLoggingOut.collectAsState()
  val logoutProgress by viewModel.logoutProgress.collectAsState()
  val logoutStatusMessage by viewModel.logoutStatusMessage.collectAsState()
  val showLogoutConfirm by viewModel.showLogoutConfirm.collectAsState()
  val currentLanguage by viewModel.currentLanguage.collectAsState()

  CompositionLocalProvider(LocalAppLanguage provides currentLanguage) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .background(Slate50)
    ) {
    val isWideScreen = maxWidth >= 680.dp
    val isDesktopMode = forceDesktop ?: isWideScreen

    // Determine if user is in an authenticated app flow
    val isAuthFlow = currentScreen == AppScreen.SPLASH ||
      currentScreen == AppScreen.ONBOARDING ||
      currentScreen == AppScreen.LOGIN ||
      currentScreen == AppScreen.REGISTER ||
      currentScreen == AppScreen.ADMIN_LOGIN

    if (isAuthFlow || currentUser == null && currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.REGISTER && currentScreen != AppScreen.ADMIN_LOGIN) {
      // Clean full-screen auth / onboarding views
      Crossfade(targetState = currentScreen, label = "AuthNavigation") { screen ->
        when (screen) {
          AppScreen.SPLASH -> SplashScreen(viewModel)
          AppScreen.ONBOARDING -> OnboardingScreen(viewModel)
          AppScreen.REGISTER -> RegisterScreen(viewModel)
          AppScreen.ADMIN_LOGIN -> AdminLoginScreen(viewModel)
          else -> LoginScreen(viewModel)
        }
      }
    } else {
      // Main App Layout (Mobile Bottom Navigation or Desktop Sidebar Navigation)
      if (isDesktopMode) {
        // DESKTOP / TABLET DUAL-PANE SHELL
        Row(modifier = Modifier.fillMaxSize()) {
          FinGuideSidebar(
            currentScreen = currentScreen,
            onNavigate = { viewModel.navigateTo(it) },
            userRole = currentUser?.role ?: "USER",
            onLogout = { viewModel.requestLogout() },
            currentLanguage = currentLanguage
          )

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
          ) {
            MainScreenContent(
              currentScreen = currentScreen,
              viewModel = viewModel,
              isDesktopView = true
            )
          }
        }
      } else {
        // MOBILE SHELL (Content with Bottom Navigation Bar)
        val showBottomBar = currentScreen != AppScreen.ADD_EXPENSE &&
          currentScreen != AppScreen.ADD_INCOME &&
          currentScreen != AppScreen.VOICE_EXPENSE &&
          currentScreen != AppScreen.LEARN_DETAIL &&
          currentScreen != AppScreen.RESOURCE_DETAIL &&
          currentScreen != AppScreen.ADMIN_DASHBOARD &&
          currentScreen != AppScreen.ADMIN_USERS &&
          currentScreen != AppScreen.ADMIN_RESOURCES &&
          currentScreen != AppScreen.ADMIN_REPORTS

        Scaffold(
          bottomBar = {
            if (showBottomBar) {
              FinGuideBottomNavBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) },
                currentLanguage = currentLanguage
              )
            }
          },
          contentWindowInsets = WindowInsets(0.dp)
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            MainScreenContent(
              currentScreen = currentScreen,
              viewModel = viewModel,
              isDesktopView = false
            )
          }
        }
      }
    }

    // FinGuide Global Menubar Drawer / Sheet
    if (isMenuBarOpen) {
      FinGuideMenuBarSheet(
        viewModel = viewModel,
        onDismiss = { viewModel.closeMenuBar() }
      )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
      LogoutConfirmationDialog(
        onConfirm = { viewModel.executeLogout() },
        onDismiss = { viewModel.cancelLogout() }
      )
    }

    // Logout Progress Dialog
    if (isLoggingOut) {
      LogoutProgressDialog(
        progress = logoutProgress,
        statusMessage = logoutStatusMessage
      )
    }
  }
}
}

@Composable
fun MainScreenContent(
  currentScreen: AppScreen,
  viewModel: FinGuideViewModel,
  isDesktopView: Boolean
) {
  Crossfade(targetState = currentScreen, label = "MainNavigation") { screen ->
    when (screen) {
      AppScreen.DASHBOARD -> DashboardScreen(viewModel, isDesktopView = isDesktopView)
      AppScreen.FINANCE -> FinanceScreen(viewModel)
      AppScreen.ADD_EXPENSE -> AddExpenseScreen(viewModel)
      AppScreen.ADD_INCOME -> AddIncomeScreen(viewModel)
      AppScreen.VOICE_EXPENSE -> VoiceExpenseScreen(viewModel)
      AppScreen.BUDGET_SAVINGS -> BudgetSavingsScreen(viewModel)
      AppScreen.AI_ASSISTANT -> AiAssistantScreen(viewModel)
      AppScreen.LEARN -> LearnScreen(viewModel)
      AppScreen.LEARN_DETAIL -> LearnDetailScreen(viewModel)
      AppScreen.RESOURCES -> ResourceLocatorScreen(viewModel)
      AppScreen.RESOURCE_DETAIL -> ResourceDetailScreen(viewModel)
      AppScreen.DOCUMENTS -> DocumentsScreen(viewModel)
      AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel)
      AppScreen.PROFILE -> ProfileScreen(viewModel)
      AppScreen.ADMIN_DASHBOARD,
      AppScreen.ADMIN_USERS,
      AppScreen.ADMIN_RESOURCES,
      AppScreen.ADMIN_VERIFICATION,
      AppScreen.ADMIN_EDUCATION,
      AppScreen.ADMIN_REPORTS,
      AppScreen.ADMIN_NOTIFICATIONS,
      AppScreen.ADMIN_SETTINGS -> AdminPortalScreen(viewModel)
      else -> DashboardScreen(viewModel, isDesktopView = isDesktopView)
    }
  }
}
