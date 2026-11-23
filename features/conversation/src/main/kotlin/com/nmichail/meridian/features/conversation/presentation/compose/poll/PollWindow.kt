package com.nmichail.meridian.features.conversation.presentation.compose.poll

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

internal class SheetHeight(
	val fraction: Float,
	val slide: (Float) -> Unit,
)

@Composable
@Suppress("MagicNumber")
internal fun rememberSheetHeight(): SheetHeight {
	var fraction by remember { mutableFloatStateOf(value = 0.52f) }
	val slide = { next: Float ->
		fraction = next
	}

	return SheetHeight(fraction = fraction, slide = slide)
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