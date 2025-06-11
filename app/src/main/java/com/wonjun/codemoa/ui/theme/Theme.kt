package com.wonjun.codemoa.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = CodeMOAPrimary,
    onPrimary = Color.White,
    primaryContainer = CodeMOAPrimaryContainer,
    onPrimaryContainer = CodeMOAOnPrimaryContainer,

    secondary = CodeMOASecondary,
    onSecondary = Color.White,
    secondaryContainer = CodeMOASecondaryContainer,
    onSecondaryContainer = CodeMOAOnSecondaryContainer,

    tertiary = CodeMOATertiary,
    onTertiary = Color.White,
    tertiaryContainer = CodeMOATertiaryContainer,
    onTertiaryContainer = CodeMOAOnTertiaryContainer,

    background = CodeMOABackground,
    onBackground = CodeMOAOnBackground,

    surface = CodeMOASurface,
    onSurface = CodeMOAOnSurface,
    surfaceVariant = CodeMOASurfaceVariant,
    onSurfaceVariant = CodeMOAOnSurfaceVariant,

    error = CodeMOAError,
    onError = Color.White,
    errorContainer = CodeMOAErrorContainer,
    onErrorContainer = CodeMOAOnErrorContainer,

    outline = CodeMOAOutline,
    outlineVariant = CodeMOAOutlineVariant
)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = CodeMOAPrimaryDark,
    onPrimary = CodeMOAOnPrimaryDark,
    primaryContainer = CodeMOAPrimaryContainerDark,
    onPrimaryContainer = CodeMOAOnPrimaryContainerDark,

    secondary = CodeMOASecondaryDark,
    onSecondary = CodeMOAOnSecondaryDark,
    secondaryContainer = CodeMOASecondaryContainerDark,
    onSecondaryContainer = CodeMOAOnSecondaryContainerDark,

    tertiary = CodeMOATertiaryDark,
    onTertiary = CodeMOAOnTertiaryDark,
    tertiaryContainer = CodeMOATertiaryContainerDark,
    onTertiaryContainer = CodeMOAOnTertiaryContainerDark,

    background = CodeMOABackgroundDark,
    onBackground = CodeMOAOnBackgroundDark,

    surface = CodeMOASurfaceDark,
    onSurface = CodeMOAOnSurfaceDark,
    surfaceVariant = CodeMOASurfaceVariantDark,
    onSurfaceVariant = CodeMOAOnSurfaceVariantDark,

    error = CodeMOAErrorDark,
    onError = CodeMOAOnErrorDark,
    errorContainer = CodeMOAErrorContainerDark,
    onErrorContainer = CodeMOAOnErrorContainerDark,

    outline = CodeMOAOutlineDark,
    outlineVariant = CodeMOAOutlineVariantDark
)

@Composable
fun CodeMOATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}