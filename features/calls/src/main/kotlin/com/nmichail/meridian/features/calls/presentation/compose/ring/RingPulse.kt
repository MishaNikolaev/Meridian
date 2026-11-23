package com.nmichail.meridian.features.calls.presentation.compose.ring

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.palette

private val outer = 232.dp
private val inner = 192.dp
private const val OUTER_ALPHA = 0.08f
private const val INNER_ALPHA = 0.14f
private const val PULSE_MS = 1600
private const val OUTER_GROW = 1.06f
private const val INNER_GROW = 1.04f
private const val INNER_DELAY = 200

@Composable
internal fun RingPulse(modifier: Modifier = Modifier) {
	val transition = rememberInfiniteTransition(label = "ring-pulse")
	val outerScale by transition.animateFloat(
		initialValue = 1f,
		targetValue = OUTER_GROW,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = PULSE_MS, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse,
		),
		label = "outer",
	)
	val innerScale by transition.animateFloat(
		initialValue = 1f,
		targetValue = INNER_GROW,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = PULSE_MS, delayMillis = INNER_DELAY, easing = FastOutSlowInEasing),
			repeatMode = RepeatMode.Reverse,
		),
		label = "inner",
	)
	Box(modifier = modifier.size(outer), contentAlignment = Alignment.Center) {
		PulseDisk(size = outer, alpha = OUTER_ALPHA, scale = outerScale)
		PulseDisk(size = inner, alpha = INNER_ALPHA, scale = innerScale)
	}
}

@Composable
private fun PulseDisk(size: Dp, alpha: Float, scale: Float) {
	Box(
		modifier = Modifier
			.size(size)
			.scale(scale)
			.clip(CircleShape)
			.background(palette.accentPrimary.copy(alpha = alpha)),
	)
}
