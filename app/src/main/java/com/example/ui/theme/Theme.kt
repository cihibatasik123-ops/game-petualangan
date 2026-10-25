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

private val AdventureDarkColorScheme = darkColorScheme(
    primary = GoldAmberPrimary,
    onPrimary = Color(0xFF1E1300),
    primaryContainer = Color(0xFF452B00),
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = ArcaneCyan,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = Color(0xFF004E5B),
    onSecondaryContainer = Color(0xFFB5EFFF),
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    background = SlateDark900,
    onBackground = TextPrimaryLight,
    surface = SlateDark800,
    onSurface = TextPrimaryLight,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondaryLight,
    outline = DarkCardStroke,
    error = RubyRed
)

private val AdventureLightColorScheme = lightColorScheme(
    primary = GoldAmberSecondary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = Color(0xFF261900),
    secondary = ArcaneCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7F4FF),
    onSecondaryContainer = Color(0xFF001F25),
    tertiary = EmeraldGreen,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = RubyRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to immersive dark adventure theme
    dynamicColor: Boolean = false, // Keep consistent fantasy branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AdventureDarkColorScheme else AdventureLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
