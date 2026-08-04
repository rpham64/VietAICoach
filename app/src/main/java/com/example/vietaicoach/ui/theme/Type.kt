package com.example.vietaicoach.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.example.vietaicoach.R

/**
 * Nunito — the design's typeface, bundled as the upstream variable font
 * (`res/font/nunito.ttf`, SIL OFL 1.1; licence at `res/raw/nunito_ofl.txt`).
 *
 * One file covers every weight through the `wght` axis rather than shipping four static
 * cuts. Variable axes need API 26+ and `minSdk` is 30. Nunito's Vietnamese coverage is
 * what makes it the right pick here — the UI is full of diacritics.
 */
@OptIn(ExperimentalTextApi::class)
private fun nunito(weight: FontWeight) = Font(
    resId = R.font.nunito,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight))
)

val Nunito = FontFamily(
    nunito(FontWeight.Normal),     // 400 — body
    nunito(FontWeight.SemiBold),   // 600
    nunito(FontWeight.Bold),       // 700
    nunito(FontWeight.ExtraBold)   // 800 — wordmark, section labels
)

/**
 * Material's default type ramp with Nunito swapped in. Sizes, line heights and tracking are
 * left alone — the design only ever varies family and weight.
 */
private val default = Typography()

val Typography = Typography(
    displayLarge = default.displayLarge.copy(fontFamily = Nunito),
    displayMedium = default.displayMedium.copy(fontFamily = Nunito),
    displaySmall = default.displaySmall.copy(fontFamily = Nunito),
    headlineLarge = default.headlineLarge.copy(fontFamily = Nunito),
    headlineMedium = default.headlineMedium.copy(fontFamily = Nunito),
    headlineSmall = default.headlineSmall.copy(fontFamily = Nunito),
    titleLarge = default.titleLarge.copy(fontFamily = Nunito),
    titleMedium = default.titleMedium.copy(fontFamily = Nunito),
    titleSmall = default.titleSmall.copy(fontFamily = Nunito),
    bodyLarge = default.bodyLarge.copy(fontFamily = Nunito),
    bodyMedium = default.bodyMedium.copy(fontFamily = Nunito),
    bodySmall = default.bodySmall.copy(fontFamily = Nunito),
    labelLarge = default.labelLarge.copy(fontFamily = Nunito),
    labelMedium = default.labelMedium.copy(fontFamily = Nunito),
    labelSmall = default.labelSmall.copy(fontFamily = Nunito)
)
