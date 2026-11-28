package com.nmichail.meridian.features.calls.catalog.data.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build

internal class CallAudioRoute(context: Context) {
	private val audio = context.getSystemService(AudioManager::class.java)
	private val previousMode = audio.mode
	private val previousSpeaker = audio.isSpeakerphoneOn
	private val previousDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		audio.communicationDevice
	} else {
		null
	}
	private var focus: AudioFocusRequest? = null

	fun start(speaker: Boolean, attributes: AudioAttributes) {
		val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
			.setAudioAttributes(attributes)
			.build()
		focus = request
		audio.requestAudioFocus(request)
		selectSpeaker(speaker)
		if (audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL) == 0) {
			val volume = audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) / 2
			audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, 0)
		}
	}

	fun resume() {
		audio.mode = AudioManager.MODE_IN_COMMUNICATION
	}

	@Suppress("DEPRECATION")
	fun selectSpeaker(speaker: Boolean) {
		resume()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val wanted = if (speaker) AudioDeviceInfo.TYPE_BUILTIN_SPEAKER else AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
			val device = audio.availableCommunicationDevices.firstOrNull { it.type == wanted }
			if (device != null && audio.setCommunicationDevice(device)) {
				return
			}
			audio.clearCommunicationDevice()
		}
		audio.isSpeakerphoneOn = speaker
	}

	@Suppress("DEPRECATION")
	fun stop() {
		focus?.let(audio::abandonAudioFocusRequest)
		focus = null
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			audio.clearCommunicationDevice()
			previousDevice?.let(audio::setCommunicationDevice)
		} else {
			audio.isSpeakerphoneOn = previousSpeaker
		}
		audio.mode = previousMode
	}
}