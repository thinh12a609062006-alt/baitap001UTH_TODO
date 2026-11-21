package com.example.baitap001uth_todo.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.example.baitap001uth_todo.viewmodel.AccentOption
import com.example.baitap001uth_todo.viewmodel.SettingsUiState
import com.example.baitap001uth_todo.viewmodel.ThemeMode

data class AppColors(
    val dark: Boolean,
    val accent: Color,
    val bg: Color,
    val panel: Color,
    val soft: Color,
    val  ink:  Color,
    val line: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        false, Color(0xFF2F5BEA), Color(0xFFF7F8FC), Color.White,
        Color(0xFFF3F5FC), Color(0xFF1B1F3B), Color(0xFFE3E6F0)
    )
}
val Blue: Color @Composable get() = LocalAppColors.current.accent
val Ink: Color @Composable get() = LocalAppColors.current.ink
val Soft: Color @Composable get() = LocalAppColors.current.soft
val Bg: Color @Composable get() = LocalAppColors.current.bg
val Panel: Color @Composable get() = LocalAppColors.current.panel
val Line: Color @Composable get() = LocalAppColors.current.line

fun AccentOption.color(): Color = when (this) {
    AccentOption.BLUE -> Color(0xFF2F5BEA)
    AccentOption.GREEN -> Color(0xFF1F9D6B)
    AccentOption.ORANGE -> Color(0xFFC2410C)
    AccentOption.PURPLE -> Color(0xFF7C3AED)
}

private fun palette(dark: Boolean, accent: Color) =
    if (dark) AppColors(
        true, accent, Color(0xFF0F1115), Color(0xFF1A1D24),
        Color(0xFF242833), Color(0xFFEDEFF7), Color(0xFF2E3340)
    ) else AppColors(
        false, accent, Color(0xFFF7F8FC), Color.White,
        Color(0xFFF3F5FC), Color(0xFF1B1F3B), Color(0xFFE3E6F0)
    )

private fun Context.hostActivity(): Activity? {
    var c = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

@Composable
fun UthAppTheme(settings: SettingsUiState, content: @Composable () -> Unit) {
    val systemDark = isSystemInDarkTheme()
    val dark = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> systemDark
    }
    val c = palette(dark, settings.accent.color())
    val scale = 0.85f + 0.5f * settings.fontSize
    val density = LocalDensity.current

    val scheme = if (dark) darkColorScheme(
        primary = c.accent, onPrimary = Color.White,
        secondaryContainer = c.accent.copy(alpha = 0.25f), onSecondaryContainer = c.ink,
        background = c.bg, onBackground = c.ink,
        surface = c.panel, onSurface = c.ink,
        surfaceVariant = c.soft, onSurfaceVariant = Color(0xFF9AA0B4),
        outline = c.line, outlineVariant = c.line,
        surfaceContainerLowest = c.panel, surfaceContainerLow = c.panel,
        surfaceContainer = c.panel, surfaceContainerHigh = c.panel, surfaceContainerHighest = c.soft
    ) else lightColorScheme(
        primary = c.accent, onPrimary = Color.White,
        secondaryContainer = c.accent.copy(alpha = 0.15f), onSecondaryContainer = c.ink,
        background = c.bg, onBackground = c.ink,
        surface = c.panel, onSurface = c.ink,
        surfaceVariant = c.soft, onSurfaceVariant = Color(0xFF6B7280),
        outline = c.line, outlineVariant = c.line,
        surfaceContainerLowest = c.panel, surfaceContainerLow = c.panel,
        surfaceContainer = c.panel, surfaceContainerHigh = c.panel, surfaceContainerHighest = c.soft
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            view.context.hostActivity()?.window?.let { window ->
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !dark
                controller.isAppearanceLightNavigationBars = !dark
            }
        }
    }

    CompositionLocalProvider(
        LocalAppColors provides c,
        LocalDensity provides Density(density.density, fontScale = density.fontScale * scale)
    ) {
        MaterialTheme(colorScheme = scheme) {
            Surface(modifier = Modifier.fillMaxSize(), color = c.bg, contentColor = c.ink, content = content)
        }
    }
}