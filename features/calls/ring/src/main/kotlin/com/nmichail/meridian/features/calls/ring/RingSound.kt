package com.nmichail.meridian.features.calls.ring

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.io.IOException

private const val BUZZ = 800L
private const val PAUSE = 400L
private const val END_MS = 700
private const val END_VOLUME = 90
private val PATTERN = longArrayOf(0L, BUZZ, PAUSE, BUZZ)

class RingSound(context: Context) {
	private val app = context.applicationContext
	private var player: MediaPlayer? = null
	private var tone: ToneGenerator? = null

	fun play(incoming: Boolean) {
		stop()
		val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE) ?: return
		val usage = if (incoming) {
			AudioAttributes.USAGE_NOTIFICATION_RINGTONE
		} else {
			AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING
		}
		val next = MediaPlayer()
		try {
			next.setAudioAttributes(
				AudioAttributes.Builder()
					.setUsage(usage)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.build(),
			)
			next.setDataSource(app, uri)
			next.isLooping = true
			next.prepare()
			next.start()
			player = next
		} catch (_: IOException) {
			next.release()
		} catch (_: IllegalStateException) {
			next.release()
		} catch (_: IllegalArgumentException) {
			next.release()
		} catch (_: SecurityException) {
			next.release()
		}
		if (incoming) {
			pulse()
		}
	}

	fun playEnd() {
		stop()
		try {
			val next = ToneGenerator(AudioManager.STREAM_VOICE_CALL, END_VOLUME)
			tone = next
			next.startTone(ToneGenerator.TONE_CDMA_NETWORK_BUSY, END_MS)
		} catch (_: RuntimeException) {
			Unit
		}
	}

	fun stop() {
		val current = player
		player = null
		if (current != null) {
			try {
				current.stop()
			} catch (_: IllegalStateException) {
				Unit
			}
			current.release()
		}
		tone?.release()
		tone = null
		vibrator()?.cancel()
	}

	private fun pulse() {
		val effect = VibrationEffect.createWaveform(PATTERN, 0)
		vibrator()?.vibrate(effect)
	}

	private fun vibrator(): Vibrator? =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			app.getSystemService(VibratorManager::class.java)?.defaultVibrator
		} else {
			@Suppress("DEPRECATION")
			app.getSystemService(Vibrator::class.java)
		}
}
