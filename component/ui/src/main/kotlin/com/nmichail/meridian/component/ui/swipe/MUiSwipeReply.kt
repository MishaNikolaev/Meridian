package com.nmichail.meridian.component.ui.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.theme.palette
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun MUiSwipeReply(
	enabled: Boolean,
	onReply: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	var drag by remember { mutableFloatStateOf(0f) }
	var armed by remember { mutableStateOf(false) }
	var settle by remember { mutableStateOf<Job?>(null) }
	val scope = rememberCoroutineScope()
	val haptic = LocalHapticFeedback.current
	val density = LocalDensity.current
	val trigger = with(density) { 64.dp.toPx() }
	val limit = with(density) { 80.dp.toPx() }
	Box(modifier = modifier.fillMaxWidth()) {
		ReplyMark(progress = (-drag / trigger).coerceIn(0f, 1f))

		Box(
			modifier = Modifier
				.offset { IntOffset(drag.roundToInt(), 0) }
				.dragReply(
					enabled = enabled,
					limit = limit,
					trigger = trigger,
					onMove = { next, crossed ->
						settle?.cancel()
						drag = next
						if (crossed && !armed) {
							armed = true
							haptic.performHapticFeedback(HapticFeedbackType.LongPress)
						}
						if (!crossed) {
							armed = false
						}
					},
					onRelease = { reply ->
						armed = false
						val from = drag
						settle = scope.launch {
							Animatable(from).animateTo(0f) { drag = value }
						}
						if (reply) {
							onReply()
						}
					},
				),
		) {
			content()
		}
	}
}

@Composable
private fun BoxScope.ReplyMark(progress: Float) {
	Image(
		painter = painterResource(R.drawable.ic_message_reply),
		contentDescription = null,
		colorFilter = ColorFilter.tint(palette.accentPrimary),
		modifier = Modifier
			.align(Alignment.CenterEnd)
			.padding(end = 16.dp)
			.size(22.dp)
			.graphicsLayer { alpha = progress },
	)
}

private fun Modifier.dragReply(
	enabled: Boolean,
	limit: Float,
	trigger: Float,
	onMove: (Float, Boolean) -> Unit,
	onRelease: (Boolean) -> Unit,
): Modifier = pointerInput(enabled, limit, trigger) {
	if (!enabled) {
		return@pointerInput
	}
	var offset = 0f
	detectHorizontalDragGestures(
		onDragCancel = {
			offset = 0f
			onRelease(false)
		},
		onDragEnd = {
			val reply = offset <= -trigger
			offset = 0f
			onRelease(reply)
		},
		onHorizontalDrag = { change, amount ->
			change.consume()
			offset = (offset + amount).coerceIn(-limit, 0f)
			onMove(offset, offset <= -trigger)
		},
	)
}
