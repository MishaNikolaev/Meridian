package com.nmichail.meridian.features.conversation.presentation.compose.voice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

internal class VoiceCapture(context: Context) {
	private val context = context.applicationContext
	private var recorder: MediaRecorder? = null
	private var file: File? = null
	private var startedAt = 0L

	private companion object {
		const val SECOND = 1000L
		const val CHANNELS = 1
		const val SAMPLE_RATE = 44_100
		const val BIT_RATE = 96_000
	}

	fun start(): Boolean {
		cancel()
		val target = File(context.cacheDir, "voice.m4a")
		val next = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			MediaRecorder(context)
		} else {
			@Suppress("DEPRECATION")
			MediaRecorder()
		}
		return try {
			next.setAudioSource(MediaRecorder.AudioSource.MIC)
			next.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
			next.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
			next.setAudioChannels(CHANNELS)
			next.setAudioSamplingRate(SAMPLE_RATE)
			next.setAudioEncodingBitRate(BIT_RATE)
			next.setOutputFile(target.absolutePath)
			next.prepare()
			next.start()
			recorder = next
			file = target
			startedAt = android.os.SystemClock.elapsedRealtime()
			true
		} catch (_: RuntimeException) {
			next.release()
			target.delete()
			false
		} catch (_: java.io.IOException) {
			next.release()
			target.delete()
			false
		}
	}

	fun level(): Int = try {
		recorder?.maxAmplitude ?: 0
	} catch (_: RuntimeException) {
		0
	}

	fun seconds(): Int {
		if (startedAt == 0L) {
			return 0
		}
		return ((android.os.SystemClock.elapsedRealtime() - startedAt) / SECOND).toInt()
	}

	fun finish(): ByteArray? {
		val stored = file
		release()
		val bytes = stored?.takeIf { item -> item.exists() }?.readBytes()
		stored?.delete()
		return bytes?.takeIf { item -> item.isNotEmpty() }
	}

	fun cancel() {
		val stored = file
		release()
		stored?.delete()
	}

	private fun release() {
		val current = recorder ?: return
		recorder = null
		file = null
		try {
			current.stop()
		} catch (_: RuntimeException) {
			Unit
		}
		current.release()
	}
}