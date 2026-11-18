package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class SheetHeight(
	val fraction: Float,
	val approach: (Float) -> Unit,
	val slide: (Float) -> Unit,
)

@Composable
@Suppress("MagicNumber")
internal fun rememberSheetHeight(open: Boolean): SheetHeight {
	var fraction by remember { mutableFloatStateOf(value = 0.52f) }
	val scope = rememberCoroutineScope()
	val motion = remember { arrayOfNulls<Job>(1) }
	val approach = { target: Float ->
		motion[0]?.cancel()
		motion[0] = scope.launch {
			animate(initialValue = fraction, targetValue = target, animationSpec = tween(durationMillis = 280)) { value, _ ->
				fraction = value
			}
		}
	}
	val slide = { next: Float ->
		motion[0]?.cancel()
		fraction = next
	}

	LaunchedEffect(open) {
		approach(if (open) 0.92f else 0.52f)
	}

	return SheetHeight(fraction = fraction, approach = approach, slide = slide)
}

@Composable
@Suppress("MagicNumber")
internal fun rememberPollScroll(fraction: Float, onSlide: (Float) -> Unit): NestedScrollConnection {
	val size = rememberUpdatedState(fraction)
	val slide = rememberUpdatedState(onSlide)
	val span = rememberUpdatedState(
		with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() },
	)

	return remember {
		object : NestedScrollConnection {
			override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
				val height = span.value
				val current = size.value
				if (available.y >= 0f || current >= 1f || height <= 0f) {
					return Offset.Zero
				}
				val next = (current - available.y / height).coerceAtMost(1f)
				slide.value(next)
				return Offset(x = 0f, y = (current - next) * height)
			}

			override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
				val height = span.value
				val current = size.value
				if (available.y <= 0f || current <= 0.52f || height <= 0f) {
					return Offset.Zero
				}
				val next = (current - available.y / height).coerceAtLeast(0.52f)
				slide.value(next)
				return Offset(x = 0f, y = (current - next) * height)
			}
		}
	}
}
