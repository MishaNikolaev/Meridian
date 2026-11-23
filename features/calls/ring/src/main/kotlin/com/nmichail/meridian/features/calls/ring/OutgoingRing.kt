package com.nmichail.meridian.features.calls.ring

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun OutgoingRing(active: Boolean) {
	val context = LocalContext.current
	val sound = remember(context) { RingSound(context) }
	DisposableEffect(active) {
		if (active) {
			sound.play(incoming = false)
		} else {
			sound.stop()
		}
		onDispose { sound.stop() }
	}
}