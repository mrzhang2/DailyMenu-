package com.dailymenu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryYellow,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryYellowDark,
    onSecondaryContainer = TextPrimary,
    tertiary = WarmBrown,
    onTertiary = SurfaceWhite,
    tertiaryContainer = WarmCream,
    onTertiaryContainer = TextPrimary,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    background = BackgroundCream,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = WarmCream,
    onSurfaceVariant = TextSecondary,
    outline = TextSecondary.copy(alpha = 0.5f),
    outlineVariant = WarmBrown.copy(alpha = 0.2f),
    scrim = TextPrimary.copy(alpha = 0.5f),
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundCream,
    inversePrimary = PrimaryOrangeLight,
    surfaceTint = PrimaryOrange,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryOrangeLight,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryOrangeDark,
    onPrimaryContainer = SurfaceWhite,
    secondary = SecondaryYellowDark,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryYellow,
    onSecondaryContainer = TextPrimary,
    tertiary = WarmCream,
    onTertiary = TextPrimary,
    tertiaryContainer = WarmBrown,
    onTertiaryContainer = SurfaceWhite,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    background = TextPrimary,
    onBackground = BackgroundCream,
    surface = TextSecondary,
    onSurface = BackgroundCream,
    surfaceVariant = WarmBrown,
    onSurfaceVariant = WarmCream,
    outline = WarmCream.copy(alpha = 0.5f),
    outlineVariant = WarmBrown.copy(alpha = 0.3f),
    scrim = TextPrimary.copy(alpha = 0.7f),
    inverseSurface = BackgroundCream,
    inverseOnSurface = TextPrimary,
    inversePrimary = PrimaryOrangeDark,
    surfaceTint = PrimaryOrangeLight,
)

@Composable
fun DailyMenuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}