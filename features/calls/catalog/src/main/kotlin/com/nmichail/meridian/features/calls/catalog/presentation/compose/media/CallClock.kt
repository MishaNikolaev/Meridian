package com.nmichail.meridian.features.calls.catalog.presentation.compose.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nmichail.meridian.features.calls.catalog.presentation.compose.ring.RingPhase
import kotlinx.coroutines.delay

@Composable
internal fun rememberLiveSeconds(phase: RingPhase): Int {
	var elapsed by remember { mutableIntStateOf(0) }
	LaunchedEffect(phase) {
		if (phase != RingPhase.Live) {
			elapsed = 0
			return@LaunchedEffect
		}
		elapsed = 0
		while (true) {
			delay(timeMillis = 1000)
			elapsed++
		}
	}
	return elapsed
}

@Composable
internal fun rememberStageSeconds(): Int {
	var elapsed by remember { mutableIntStateOf(0) }
	LaunchedEffect(Unit) {
		elapsed = 0
		while (true) {
			delay(timeMillis = 1000)
			elapsed++
		}
	}
	return elapsed
}

private const val MINUTE = 60

internal fun callTime(seconds: Int): String {
	val minutes = seconds / MINUTE
	val rest = seconds % MINUTE
	return "%d:%02d".format(minutes, rest)
}
