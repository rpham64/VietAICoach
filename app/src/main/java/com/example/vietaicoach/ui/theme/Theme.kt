package com.example.vietaicoach.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LacquerRed,
    onPrimary = Color.White,
    primaryContainer = LacquerRed,
    onPrimaryContainer = Color.White,
    secondary = RiceGold,
    onSecondary = Color.White,
    secondaryContainer = RiceGoldContainer,
    onSecondaryContainer = OnRiceGoldContainer,
    background = Cream,
    onBackground = Ink,
    surface = CreamSurface,
    onSurface = Ink,
    surfaceVariant = Cream,
    onSurfaceVariant = InkMuted,
    outline = CreamOutline,
    outlineVariant = CreamOutline,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = LacquerRedDark,
    onPrimary = Color.White,
    primaryContainer = LacquerRedDark,
    onPrimaryContainer = Color.White,
    secondary = RiceGoldDark,
    onSecondary = Charcoal,
    secondaryContainer = RiceGoldContainerDark,
    onSecondaryContainer = OnRiceGoldContainerDark,
    background = Charcoal,
    onBackground = Bone,
    surface = CharcoalSurface,
    onSurface = Bone,
    surfaceVariant = CharcoalElevated,
    onSurfaceVariant = BoneMuted,
    outline = CharcoalOutline,
    outlineVariant = CharcoalOutline,
    error = ErrorRedDark,
    onError = Charcoal,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

/**
 * Colors the mockups use that have no Material slot: coaching-success surfaces,
 * skeleton placeholders, and the muted "in flight / failed" user bubble.
 */
data class CoachColors(
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val skeleton: Color,
    val bubblePending: Color
)

private val LightCoachColors = CoachColors(
    success = JadeGreen,
    successContainer = JadeContainer,
    onSuccessContainer = OnJadeContainer,
    skeleton = SkeletonLight,
    bubblePending = LacquerRedMuted
)

private val DarkCoachColors = CoachColors(
    success = JadeGreenDark,
    successContainer = JadeContainerDark,
    onSuccessContainer = OnJadeContainerDark,
    skeleton = SkeletonDark,
    bubblePending = LacquerRedMutedDark
)

val LocalCoachColors: ProvidableCompositionLocal<CoachColors> =
    compositionLocalOf { LightCoachColors }

/**
 * Note: dynamic color is intentionally disabled — the brand palette is the identity
 * of the product and should not be recolored by the device wallpaper.
 */
@Composable
fun VietAICoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val coachColors = if (darkTheme) DarkCoachColors else LightCoachColors

    CompositionLocalProvider(LocalCoachColors provides coachColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}