package com.nmichail.meridian.features.calls.presentation.compose.conference

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal class SpeakMeter(context: Context) {
	private val app = context.applicationContext
	private var record: AudioRecord? = null
	private var buffer = ShortArray(size = 0)

	fun start(): Boolean {
		stop()
		val allowed = ContextCompat.checkSelfPermission(
			app,
			Manifest.permission.RECORD_AUDIO,
		) == PackageManager.PERMISSION_GRANTED
		if (!allowed) {
			return false
		}
		val size = AudioRecord.getMinBufferSize(
			SAMPLE_RATE,
			AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
		)
		if (size <= 0) {
			return false
		}
		val next = AudioRecord(
			MediaRecorder.AudioSource.VOICE_COMMUNICATION,
			SAMPLE_RATE,
			AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT,
			size * 2,
		)
		if (next.state != AudioRecord.STATE_INITIALIZED) {
			next.release()
			return false
		}
		buffer = ShortArray(size)
		next.startRecording()
		record = next
		return true
	}

	fun speaking(): Boolean {
		val current = record ?: return false
		val read = try {
			current.read(buffer, 0, buffer.size)
		} catch (_: IllegalStateException) {
			return false
		}
		if (read <= 0) {
			return false
		}
		var sum = 0.0
		for (index in 0 until read) {
			val sample = buffer[index].toDouble()
			sum += sample * sample
		}

		return sqrt(sum / read) >= SPEAK_GATE
	}

	fun stop() {
		val current = record ?: return
		record = null
		if (current.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
			current.stop()
		}
		current.release()
	}
}

@Composable
internal fun rememberSpeaking(active: Boolean): Boolean {
	val context = LocalContext.current
	var speaking by remember { mutableStateOf(false) }
	val meter = remember(context) { SpeakMeter(context) }
	DisposableEffect(Unit) {
		onDispose { meter.stop() }
	}

	LaunchedEffect(active) {
		if (!active) {
			speaking = false
			meter.stop()
			return@LaunchedEffect
		}

		if (!meter.start()) {
			speaking = false
			return@LaunchedEffect
		}

		while (isActive) {
			speaking = meter.speaking()
			delay(timeMillis = 80L)
		}
	}
	return speaking
}

private const val SAMPLE_RATE = 16_000
private const val SPEAK_GATE = 1_200