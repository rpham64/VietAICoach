package com.example.vietaicoach.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vietaicoach.ui.theme.VietAICoachTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** How long the splash stays up. The intro settles at ~1550ms, leaving a beat before handoff. */
private const val SPLASH_DURATION_MS = 2000L

/** `splashPop` — spring overshoot on the emblem. The >1 control point supplies the 1.06 peak. */
private val PopEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

private const val POP_DURATION_MS = 800
private const val FADE_UP_DURATION_MS = 600
private const val GLOW_HALF_CYCLE_MS = 1500
private const val DOT_CYCLE_MS = 1300

/**
 * Animated launch screen for "Chào Bạn".
 *
 * The emblem is a lacquer-red speech bubble carrying a gold tone mark (dấu sắc) — Vietnamese
 * diacritics meeting AI conversation. A glow pulses behind it, the emblem pops in on a spring,
 * the wordmark and tagline rise in behind it, and three dots loop until the app is ready.
 *
 * @param onFinished invoked once [SPLASH_DURATION_MS] has elapsed.
 * @param animateIntro plays the one-shot pop and fade-ups. Previews pass `false` to render the
 *   settled composition — otherwise a static preview catches frame 0, where the emblem is still
 *   scaled to 0.6 at zero alpha and the text has not arrived. The loops always run.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit = {},
    animateIntro: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // The design tints the glow gold on cream and red on charcoal.
    val glowColor = if (colorScheme.isDark) {
        colorScheme.primary.copy(alpha = 0.5f)
    } else {
        colorScheme.secondary.copy(alpha = 0.55f)
    }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        onFinished()
    }

    Box(
        modifier = modifier
            .testTag("SplashScreen")
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Glow(color = glowColor)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Emblem(animateIntro = animateIntro)

            val wordmark by rememberFadeUp(delayMillis = 500, animateIntro = animateIntro)
            Text(
                text = "Chào Bạn",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground,
                modifier = Modifier
                    .padding(top = 26.dp)
                    .fadeUp(wordmark)
            )

            val tagline by rememberFadeUp(delayMillis = 700, animateIntro = animateIntro)
            Text(
                text = "Học tiếng Việt, tự nhiên hơn",
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fadeUp(tagline)
            )
        }

        val dots by rememberFadeUp(delayMillis = 950, animateIntro = animateIntro)
        LoadingDots(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .fadeUp(dots)
        )
    }
}

/** `splashGlow` — a soft radial bloom breathing behind the emblem. */
@Composable
private fun Glow(color: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splashGlow")
    val spec = infiniteRepeatable<Float>(
        animation = tween(GLOW_HALF_CYCLE_MS, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.85f,
        animationSpec = spec,
        label = "splashGlowAlpha"
    )
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = spec,
        label = "splashGlowScale"
    )

    Box(
        modifier = modifier
            .size(320.dp)
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            }
            .background(
                Brush.radialGradient(0f to color, 0.7f to Color.Transparent)
            )
    )
}

/** The speech-bubble emblem, popping in on a spring. */
@Composable
private fun Emblem(animateIntro: Boolean, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    // Pure white reads cleaner than cream on the light emblem; dark mode knocks it out to the bg.
    val discColor = if (colorScheme.isDark) colorScheme.background else Color.White

    // CSS `border-radius: 28px 28px 28px 6px` is TL/TR/BR/BL — the tail lands bottom-left.
    val bubbleShape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomEnd = 28.dp,
        bottomStart = 6.dp
    )

    val scale = remember { Animatable(if (animateIntro) 0.6f else 1f) }
    val alpha = remember { Animatable(if (animateIntro) 0f else 1f) }
    LaunchedEffect(animateIntro) {
        if (!animateIntro) return@LaunchedEffect
        launch { scale.animateTo(1f, tween(POP_DURATION_MS, easing = PopEasing)) }
        launch { alpha.animateTo(1f, tween(300)) }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
            }
            .size(92.dp)
            .shadow(
                elevation = 12.dp,
                shape = bubbleShape,
                ambientColor = colorScheme.primary,
                spotColor = colorScheme.primary
            )
            .clip(bubbleShape)
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        // `background(shape)` rather than `clip` — the tone mark overhangs the disc.
        Box(modifier = Modifier.size(34.dp).background(discColor, CircleShape)) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-9).dp)
                    .size(width = 6.dp, height = 14.dp)
                    .rotate(20f)
                    .background(colorScheme.secondary, RoundedCornerShape(3.dp))
            )
        }
    }
}

/** `splashDot` — three dots bouncing in sequence until the app is ready. */
@Composable
private fun LoadingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splashDots")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        repeat(3) { index ->
            val progress by transition.animateFloat(
                initialValue = 0f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = DOT_CYCLE_MS
                        0f at 0
                        1f at 520 using LinearOutSlowInEasing // 40%
                        0f at 1040 // 80%
                        0f at DOT_CYCLE_MS
                    },
                    // Offsets the phase; `delayMillis` would re-apply on every cycle.
                    initialStartOffset = StartOffset(index * 150)
                ),
                label = "splashDot$index"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 0.35f + 0.65f * progress
                        translationY = -5.dp.toPx() * progress
                    }
                    .size(7.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

/**
 * `splashFadeUp` — rise 10dp into place while fading in, after [delayMillis].
 * A `tween` holds its initial value through the delay, matching the CSS `both` fill mode.
 */
@Composable
private fun rememberFadeUp(delayMillis: Int, animateIntro: Boolean): State<Float> {
    val progress = remember { Animatable(if (animateIntro) 0f else 1f) }
    LaunchedEffect(animateIntro) {
        if (!animateIntro) return@LaunchedEffect
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = FADE_UP_DURATION_MS,
                delayMillis = delayMillis,
                easing = LinearOutSlowInEasing
            )
        )
    }
    return progress.asState()
}

/**
 * Reads darkness off the scheme rather than `isSystemInDarkTheme()`, so an explicitly-themed
 * caller (previews, a future in-app theme toggle) still gets the right variant.
 */
private val ColorScheme.isDark: Boolean
    get() = background.luminance() < 0.5f

private fun Modifier.fadeUp(progress: Float): Modifier = graphicsLayer {
    alpha = progress
    translationY = (1f - progress) * 10.dp.toPx()
}

@Preview(name = "Splash — light", showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun SplashScreenLightPreview() {
    VietAICoachTheme(darkTheme = false) {
        SplashScreen(animateIntro = false)
    }
}

@Preview(
    name = "Splash — dark",
    showBackground = true,
    widthDp = 412,
    heightDp = 892,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun SplashScreenDarkPreview() {
    VietAICoachTheme(darkTheme = true) {
        SplashScreen(animateIntro = false)
    }
}