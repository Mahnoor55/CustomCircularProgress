package com.circular.progress

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaveProgressIndicator(
    progress: Float, // 0f → 1f
    modifier: Modifier = Modifier,
    waveColor: Color = Color(0xFF2196F3),
    backgroundColor: Color = Color(0xFFE0E0E0),
    amplitude: Float = 12f,
    frequency: Float = 2f,
    speed: Float = 4f, // higher = faster horizontal wave
) {
    var phase by remember { mutableFloatStateOf(0f) }

    // Animate horizontal wave motion only if not completed
    LaunchedEffect(progress) {
        while (progress < 1f) {
            phase += speed * 0.05f
            delay(16L) // ~60 FPS
        }
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
    ) {
        val width = size.width
        val height = size.height
        val clampedProgress = progress.coerceIn(0f, 1f)

        // When progress == 1f, waveHeight = 0f → top
        val waveHeight = height * (1 - clampedProgress)

        // Background fill (bottom part)
        drawRect(color = backgroundColor, size = size)

        // If progress == 1f, fully fill and skip wave drawing
        if (clampedProgress >= 1f) {
            drawCircle(color = waveColor, radius = size.minDimension / 2, center = center)
            return@Canvas
        }

        // Build wave path
        val path = Path().apply {
            moveTo(0f, height)
            for (x in 0..width.toInt()) {
                val y = (waveHeight + amplitude * sin((x * frequency * 2 * PI / width) + phase)).toFloat()
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            close()
        }

        // Clip into circle and draw wave
        clipPath(Path().apply { addOval(Rect(Offset.Zero, size)) }) {
            drawPath(path, waveColor)
        }
    }
}


data class Bubble(
    var x: Float, var y: Float, val radius: Float, val speed: Float, val alpha: Float
)

@Composable
fun BubbleProgressIndicator(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    bubbleColor: Color = Color(0xFF2196F3),
    backgroundColor: Color = Color(0xFFE0E0E0),
    maxBubbles: Int = 20
) {
    // Store active bubbles
    var bubbles by remember { mutableStateOf<List<Bubble>>(emptyList()) }

    // Animate bubbles floating up
    LaunchedEffect(progress) {
        val random = Random(System.currentTimeMillis())
        val bubbleList = mutableListOf<Bubble>()

        while (isActive && progress < 1f) {
            // Add new bubble
            if (bubbleList.size < maxBubbles && random.nextFloat() < 0.2f) {
                bubbleList.add(
                    Bubble(
                        x = random.nextFloat(),
                        y = 1f, // start at bottom
                        radius = random.nextFloat() * 8f + 6f,
                        speed = random.nextFloat() * 0.004f + 0.002f,
                        alpha = random.nextFloat() * 0.5f + 0.5f
                    )
                )
            }

            // Update positions
            bubbleList.forEach { bubble ->
                bubble.y -= bubble.speed
            }

            // Remove bubbles that float above top
            bubbleList.removeAll { it.y < 0f }

            bubbles = bubbleList.toList()

            delay(16L) // ~60 FPS
        }
    }

    // Draw canvas
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
    ) {
        val width = size.width
        val height = size.height

        val fillHeight = height * (1 - progress.coerceIn(0f, 1f))

        // Draw background
        drawRect(color = backgroundColor, size = size)

        // Clip inside a circle
        clipPath(Path().apply {
            addOval(Rect(Offset.Zero, size))
        }) {
            // Draw progress area
            drawRect(
                color = bubbleColor.copy(alpha = 0.2f),
                topLeft = Offset(0f, fillHeight),
                size = Size(width, height - fillHeight)
            )

            // Draw bubbles only inside filled area
            bubbles.forEach { bubble ->
                val y = fillHeight + (height - fillHeight) * bubble.y
                if (y in fillHeight..height) {
                    drawCircle(
                        color = bubbleColor.copy(alpha = bubble.alpha),
                        radius = bubble.radius,
                        center = Offset(width * bubble.x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularGradientProgressBar(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF00BCD4), Color(0xFF3F51B5), Color(0xFFFF4081)),
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f),
    strokeWidth: Dp = 12.dp,
    cap: StrokeCap = StrokeCap.Round,
    animate: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(1000)
    )
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }

    Canvas(
        modifier = modifier.aspectRatio(1f)
    ) {
        val diameter = min(size.width, size.height)
        val radius = diameter / 2f
        val stroke = Stroke(width = strokeWidthPx, cap = cap)

        // Center offset
        val center = Offset(size.width / 2f, size.height / 2f)

        // Draw background circle
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(diameter, diameter),
            style = stroke
        )

        // Create gradient brush
        val brush = Brush.sweepGradient(
            colors = gradientColors, center = center
        )

        // Draw progress arc
        drawArc(
            brush = brush,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(diameter, diameter),
            style = stroke
        )
    }
}

@Composable
fun DottedCircularProgress(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    totalDots: Int = 60,
    dotRadius: Float = 8f,
    spaceAngle: Float = 6f, // spacing between dots
    activeColor: Color = Color(0xFF3F51B5),
    inactiveColor: Color = Color.LightGray.copy(alpha = 0.3f),
    animate: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(800)
    )

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val radius = size.minDimension / 2f - dotRadius * 2
        val center = Offset(size.width / 2, size.height / 2)

        val sweepPerDot = 360f / totalDots
        val activeDots = (animatedProgress * totalDots).toInt()

        repeat(totalDots) { i ->
            val angle = Math.toRadians((i * sweepPerDot - 90).toDouble())
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()

            val color = if (i < activeDots) activeColor else inactiveColor

            drawCircle(
                color = color, radius = dotRadius, center = Offset(x, y)
            )
        }
    }
}

@Composable
fun GlowingRotatingCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF00BCD4), Color(0xFF3F51B5), Color(0xFFFF4081)),
    glowColor: Color = Color(0xFF3F51B5),
    strokeWidth: Dp = 20.dp,
    glowRadius: Dp = 30.dp
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    val glowRadiusPx = with(density) { glowRadius.toPx() }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    // Infinite rotation (will stop updating when progress == 1f)
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        )
    )

    val glowPaint = remember {
        Paint().apply {
            isAntiAlias = true
            asFrameworkPaint().apply {
                maskFilter = BlurMaskFilter(glowRadiusPx, BlurMaskFilter.Blur.OUTER)
            }
        }
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val diameter = size.minDimension
        val radius = diameter / 2f
        val center = Offset(size.width / 2, size.height / 2)
        val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)

        val sweepAngle = 360f * animatedProgress
        val currentRotation = if (progress < 1f) rotation else 0f

        // Glow layer
        drawArc(
            color = glowColor.copy(alpha = 0.4f),
            startAngle = currentRotation - 5f,
            sweepAngle = sweepAngle + 10f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(diameter, diameter),
            style = stroke
        )

        // Gradient arc
        val brush = Brush.sweepGradient(
            colors = gradientColors, center = center
        )

        rotate(degrees = currentRotation, pivot = center) {
            drawArc(
                brush = brush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(diameter, diameter),
                style = stroke
            )
        }
    }
}

@Composable
fun SegmentedCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    segmentCount: Int = 40,
    activeColor: Color = Color(0xFF00E5FF),
    inactiveColor: Color = Color(0xFF37474F),
    strokeWidth: Dp = 10.dp,
    gapAngle: Float = 3f, // gap between segments
    glow: Boolean = true
) {
    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2, size.height / 2)

        val sweepPerSegment = (360f - (segmentCount * gapAngle)) / segmentCount
        val totalActiveSegments = (segmentCount * progress.coerceIn(0f, 1f)).toInt()

        val paint = Paint().apply {
            isAntiAlias = true
            if (glow) {
                asFrameworkPaint().maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
            }
        }

        for (i in 0 until segmentCount) {
            val startAngle = i * (sweepPerSegment + gapAngle) - 90f
            val color = if (i < totalActiveSegments) activeColor else inactiveColor

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepPerSegment,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun DualRingCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    outerColor: Color = Color(0xFF00E5FF),
    innerColor: Color = Color(0xFFFF4081),
    backgroundColor: Color = Color(0xFF263238),
    strokeWidth: Dp = 12.dp,
    glow: Boolean = true
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(800, easing = LinearEasing)
    )

    // Rotation animations for dynamic feel
    val infiniteTransition = rememberInfiniteTransition(label = "dualRing")
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "outer"
    )
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f, animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Restart
        ), label = "inner"
    )

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val diameter = size.minDimension
        val center = Offset(size.width / 2, size.height / 2)
        val radiusOuter = diameter / 2f
        val radiusInner = diameter / 2.5f

        val sweepAngle = 360f * animatedProgress

        // Background circles
        drawCircle(
            backgroundColor.copy(alpha = 0.2f),
            radiusOuter,
            center,
            style = Stroke(strokeWidthPx)
        )
        drawCircle(
            backgroundColor.copy(alpha = 0.2f),
            radiusInner,
            center,
            style = Stroke(strokeWidthPx)
        )

        // Glow effect
        val paint = Paint().apply {
            isAntiAlias = true
            if (glow) {
                asFrameworkPaint().maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.SOLID)
            }
        }

        // Outer arc (clockwise)
        rotate(degrees = outerRotation, pivot = center) {
            drawArc(
                color = outerColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radiusOuter, center.y - radiusOuter),
                size = Size(radiusOuter * 2, radiusOuter * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        // Inner arc (counter-clockwise)
        rotate(degrees = innerRotation, pivot = center) {
            drawArc(
                color = innerColor,
                startAngle = 90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radiusInner, center.y - radiusInner),
                size = Size(radiusInner * 2, radiusInner * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun RadialPulseCircularProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF2196F3),
    pulseColor: Color = Color(0xFF64B5F6),
    backgroundColor: Color = Color(0xFFE3F2FD),
    strokeWidth: Dp = 12.dp
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800, easing = LinearEasing),
        label = "progressAnim"
    )

    val isActive = animatedProgress < 1f

    // Pulse only if active
    val infiniteTransition = if (isActive) rememberInfiniteTransition(label = "pulse") else null

    val pulseScale = if (isActive) {
        infiniteTransition!!.animateFloat(
            initialValue = 1f,
            targetValue = 1.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAnim"
        ).value
    } else 1f

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2, size.height / 2)
        val sweepAngle = 360f * animatedProgress

        // Pulse behind the circle
        if (isActive) {
            drawCircle(
                color = pulseColor.copy(alpha = 0.25f),
                radius = radius * pulseScale,
                center = center
            )
        }

        // Background ring
        drawCircle(
            color = backgroundColor,
            radius = radius - strokeWidthPx / 2,
            center = center,
            style = Stroke(strokeWidthPx, cap = StrokeCap.Round)
        )

        // Progress ring
        drawArc(
            color = baseColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}




@Composable
fun DefaultCircularProgress(
    progress: Float,
    color: Color,
    backgroundColor: Color,
    strokeWidth: Dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(800)
    )
    val stroke = with(LocalDensity.current) { strokeWidth.toPx() }

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        drawCircle(backgroundColor, radius - stroke / 2, style = Stroke(stroke))
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(stroke, cap = StrokeCap.Round)
        )
    }
}

