package com.example.demo_sentinel_ai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// We define ONLY a Light Color Scheme for the "Institutional Trust" aesthetic
private val InstitutionalColorScheme = lightColorScheme(
    primary = TrustBlue,
    secondary = ActionBlue,
    tertiary = SafeGreen,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = SurfaceColor, // White text on Navy button
    onSecondary = SurfaceColor,
    onTertiary = SurfaceColor,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = AlertRed,
    onError = SurfaceColor
)

@Composable
fun DemosentinelaiTheme(
    // We ignore the system dark theme setting to enforce our brand aesthetic
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We disable dynamic color to maintain the "Swiss Banking" look
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = InstitutionalColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match background for clean look
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true // Dark icons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
