package com.nmichail.meridian.features.conversation.presentation.compose.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Suppress("MagicNumber")
internal suspend fun AwaitPointerEventScope.recordHold(
	context: Context,
	capture: VoiceCapture,
	ask: () -> Unit,
	onRecording: (Boolean) -> Unit,
	onLevel: (Float) -> Unit,
	onVoice: (ByteArray, Int) -> Unit,
) {
	awaitFirstDown()
	val allowed = ContextCompat.checkSelfPermission(
		context,
		Manifest.permission.RECORD_AUDIO,
	) == PackageManager.PERMISSION_GRANTED
	if (!allowed) {
		ask()
		return
	}
	if (!capture.start()) {
		return
	}
	onRecording(true)
	val began = android.os.SystemClock.elapsedRealtime()
	val cancel = dragCancel()
	val held = android.os.SystemClock.elapsedRealtime() - began
	val length = capture.seconds().coerceAtLeast(1)
	val bytes = if (cancel) {
		capture.cancel()
		null
	} else {
		capture.finish()
	}
	if (!cancel && held >= 400L && bytes != null) {
		onVoice(bytes, length)
	}
	onRecording(false)
	onLevel(0f)
}

internal suspend fun AwaitPointerEventScope.tapSend(onSend: () -> Unit) {
	val down = awaitFirstDown()
	down.consume()
	var held = true
	while (held) {
		val change = awaitPointerEvent().changes.firstOrNull { item -> item.id == down.id }
		held = change != null && change.pressed
	}
	onSend()
}

@Suppress("MagicNumber")
private suspend fun AwaitPointerEventScope.dragCancel(): Boolean {
	val down = currentEvent.changes.first()
	var cancel = false
	var held = true
	while (held) {
		val change = awaitPointerEvent().changes.firstOrNull { item -> item.id == down.id }
		held = change != null && change.pressed
		if (change != null && change.pressed && change.position.x - down.position.x < -72.dp.toPx()) {
			cancel = true
		}
		change?.consume()
	}
	return cancel
}