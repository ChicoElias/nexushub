package com.nexushub.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Brand colors ──────────────────────────────────────────────────────────────

val NexusPrimary       = Color(0xFF4F46E5)   // Indigo-600
val NexusOnPrimary     = Color(0xFFFFFFFF)
val NexusPrimaryContainer  = Color(0xFFE0E7FF)
val NexusSecondary     = Color(0xFF0EA5E9)   // Sky-500
val NexusBackground    = Color(0xFFF8FAFC)
val NexusSurface       = Color(0xFFFFFFFF)
val NexusError         = Color(0xFFDC2626)

val NexusPrimaryDark           = Color(0xFF818CF8)
val NexusBackgroundDark        = Color(0xFF0F172A)
val NexusSurfaceDark           = Color(0xFF1E293B)

// ── Color schemes ─────────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary          = NexusPrimary,
    onPrimary        = NexusOnPrimary,
    primaryContainer = NexusPrimaryContainer,
    secondary        = NexusSecondary,
    background       = NexusBackground,
    surface          = NexusSurface,
    error            = NexusError,
    surfaceVariant   = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B)
)

private val DarkColorScheme = darkColorScheme(
    primary          = NexusPrimaryDark,
    onPrimary        = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    secondary        = NexusSecondary,
    background       = NexusBackgroundDark,
    surface          = NexusSurfaceDark,
    error            = Color(0xFFFCA5A5),
    surfaceVariant   = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8)
)

// ── Theme composable ──────────────────────────────────────────────────────────

@Composable
fun NexusHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NexusHubTypography,
        content = content
    )
}
