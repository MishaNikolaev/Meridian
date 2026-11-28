package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFill
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.CallVideo
import com.nmichail.meridian.features.calls.catalog.presentation.CallsDestination
import com.nmichail.meridian.features.calls.catalog.presentation.CallsState
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.MeetingPreview
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.LocalCallCapture
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.WithCallCamera
import kotlin.math.roundToInt

@Composable
fun MeetingOverlay(
	state: CallsState,
	preview: MeetingPreview,
	onBounds: (Rect?) -> Unit,
	onOpen: () -> Unit,
) {
	val content = state as? CallsState.Content ?: return
	if (content.screen != CallsDestination.Meeting) {
		return
	}
	val label = content.profilePhoto?.name.orEmpty().ifBlank { stringResource(R.string.calls_you) }
	WithCallCamera {
		val capture = LocalCallCapture.current
		DisposableEffect(capture, preview) {
			preview.capture = capture
			onDispose {
				if (preview.capture === capture) {
					preview.capture = null
				}
			}
		}
		LaunchedEffect(capture, content.cameraEnabled) {
			capture?.setCamera(content.cameraEnabled)
		}
		if (!content.meetingVisible) {
			DisposableEffect(Unit) {
				onDispose { onBounds(null) }
			}
			FloatingCall(
				camera = content.cameraEnabled,
				name = label,
				photoId = content.profilePhoto?.photoId.orEmpty(),
				onBounds = onBounds,
				onOpen = onOpen,
			)
		}
	}
}

@Composable
private fun FloatingCall(
	camera: Boolean,
	name: String,
	photoId: String,
	onBounds: (Rect?) -> Unit,
	onOpen: () -> Unit,
) {
	var offset by remember { mutableStateOf<IntOffset?>(null) }
	BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
		val density = LocalDensity.current
		val width = with(density) { 112.dp.roundToPx() }
		val height = with(density) { 158.dp.roundToPx() }
		val margin = with(density) { 16.dp.roundToPx() }
		val limitX = (constraints.maxWidth - width).coerceAtLeast(0)
		val limitY = (constraints.maxHeight - height).coerceAtLeast(0)
		val shown = offset ?: IntOffset(
			x = (limitX - margin).coerceAtLeast(0),
			y = (limitY - margin).coerceAtLeast(0),
		)
		Box(
			modifier = Modifier
				.offset { shown }
				.size(width = 112.dp, height = 158.dp)
				.clip(RoundedCornerShape(16.dp))
				.onGloballyPositioned { coordinates -> onBounds(coordinates.boundsInRoot()) }
				.pointerInput(limitX, limitY) {
					awaitEachGesture {
						val down = awaitFirstDown()
						val open = followDrag(
							start = down.id,
							limitX = limitX,
							limitY = limitY,
							origin = offset ?: shown,
							move = { next -> offset = next },
						)
						if (open) {
							onOpen()
						}
					}
				},
		) {
			if (camera) {
				CallVideo()
			} else {
				PhotoFill(name = name, modifier = Modifier, photoId = photoId)
			}
		}
	}
}

private suspend fun AwaitPointerEventScope.followDrag(
	start: PointerId,
	limitX: Int,
	limitY: Int,
	origin: IntOffset,
	move: (IntOffset) -> Unit,
): Boolean {
	var moved = false
	var current = origin
	while (true) {
		val event = awaitPointerEvent()
		val change = event.changes.firstOrNull { it.id == start }
		if (change == null || !change.pressed) {
			return !moved && change != null
		}
		val delta = change.positionChange()
		if (delta.getDistance() > 2f) {
			moved = true
		}
		if (moved) {
			change.consume()
			current = IntOffset(
				x = (current.x + delta.x.roundToInt()).coerceIn(0, limitX),
				y = (current.y + delta.y.roundToInt()).coerceIn(0, limitY),
			)
			move(current)
		}
	}
}