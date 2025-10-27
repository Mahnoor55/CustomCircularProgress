package com.circular.progress


import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CircularProgress(
    progress: Float,
    type: ProgressType,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF2196F3),
    secondaryColor: Color = color.copy(alpha = 0.3f),
    gradientColors: List<Color> = listOf(Color(0xFF00BCD4), Color(0xFF2196F3), Color(0xFF3F51B5)),
    backgroundColor: Color = color.copy(alpha = 0.1f),
    strokeWidth: Dp = 12.dp,
    size: Dp = 150.dp
) {
    when (type) {
        ProgressType.Default -> DefaultCircularProgress(
            progress = progress,
            color = color,
            backgroundColor = backgroundColor,
            strokeWidth = strokeWidth,
            modifier = modifier.size(size)
        )

        ProgressType.RadialPulse -> RadialPulseCircularProgress(
            progress = progress,
            baseColor = color,
            pulseColor = secondaryColor,
            backgroundColor = backgroundColor,
            strokeWidth = strokeWidth,
            modifier = modifier.size(size)
        )

        ProgressType.DualRing -> DualRingCircularProgress(
            progress = progress,
            outerColor = color,
            innerColor = secondaryColor,
            backgroundColor = backgroundColor,
            strokeWidth = strokeWidth,
            modifier = modifier.size(size)
        )

        ProgressType.Bubble -> BubbleProgressIndicator(
            progress = progress,
            bubbleColor = color,
            backgroundColor = backgroundColor.copy(alpha = 0.3f),
            maxBubbles = 25,
            modifier = modifier.size(size)
        )

        ProgressType.Segmented -> SegmentedCircularProgress(
            progress = progress,
            segmentCount = 10,
            activeColor = color,
            inactiveColor = secondaryColor.copy(alpha = 0.3f),
            strokeWidth = strokeWidth,
            gapAngle = 2f,
            glow = true,
            modifier = modifier.size(size)
        )

        ProgressType.GlowingRotating -> GlowingRotatingCircularProgress(
            progress = progress,
            gradientColors = gradientColors,
            glowColor = color,
            strokeWidth = strokeWidth,
            glowRadius = 40.dp,
            modifier = modifier.size(size)
        )

        ProgressType.Dotted -> DottedCircularProgress(
            progress = progress,
            totalDots = 50,
            dotRadius = 6f,
            activeColor = color,
            inactiveColor = secondaryColor.copy(alpha = 0.3f),
            modifier = modifier.size(size)
        )

        ProgressType.Gradient -> CircularGradientProgressBar(
            progress = progress,
            gradientColors = gradientColors,
            strokeWidth = strokeWidth,
            modifier = modifier.size(size)
        )

        ProgressType.Wave -> WaveProgressIndicator(
            progress = progress,
            waveColor = color,
            backgroundColor=backgroundColor.copy(alpha = 0.3f),
            amplitude = 14f,
            frequency = 2f,
            speed = 5f,
            modifier = modifier.size(size)
        )
    }
}
