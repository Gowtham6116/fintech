package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = PureWhite,
  primaryContainer = EmeraldSurface,
  onPrimaryContainer = EmeraldDark,
  secondary = TealSecondary,
  onSecondary = PureWhite,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = TealDark,
  tertiary = GoldAccent,
  onTertiary = PureWhite,
  tertiaryContainer = GoldLight,
  onTertiaryContainer = GoldDark,
  background = Slate50,
  onBackground = Slate900,
  surface = PureWhite,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate700,
  outline = Slate300,
  outlineVariant = Slate200,
  error = RedExpense,
  onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldLight,
  onPrimary = Slate900,
  primaryContainer = EmeraldDark,
  onPrimaryContainer = EmeraldSurface,
  secondary = TealLight,
  onSecondary = Slate900,
  secondaryContainer = TealDark,
  onSecondaryContainer = Color(0xFFCCFBF1),
  tertiary = GoldAccent,
  onTertiary = Slate900,
  tertiaryContainer = GoldDark,
  onTertiaryContainer = GoldLight,
  background = Slate50,
  onBackground = Slate900,
  surface = PureWhite,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate700,
  outline = Slate300,
  outlineVariant = Slate200,
  error = RedExpense,
  onError = PureWhite
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Enforce clean high-contrast fintech styling
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
