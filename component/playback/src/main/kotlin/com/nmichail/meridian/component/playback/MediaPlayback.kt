package com.nmichail.meridian.component.playback

import android.content.Context
import android.content.Intent
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MediaPlayback {
	private val state = MutableStateFlow(MediaPhase(id = "", playing = false))
	val phase: StateFlow<MediaPhase> = state

	fun play(context: Context, id: String, bytes: ByteArray, mime: String) {
		if (id.isEmpty() || bytes.isEmpty()) {
			return
		}
		val video = mime.video()
		state.value = MediaPhase(id = id, playing = true)
		val file = File(context.cacheDir, "media-$id.${if (video) "mp4" else "m4a"}")
		file.writeBytes(bytes)
		send(context, PLAY, id, file.absolutePath, video)
	}

	fun pause(context: Context) {
		state.value = state.value.copy(playing = false)
		send(context, PAUSE, "", "", video = false)
	}

	fun resume(context: Context) {
		state.value = state.value.copy(playing = true)
		send(context, RESUME, "", "", video = false)
	}

	internal fun setPhase(id: String, playing: Boolean) {
		state.value = MediaPhase(id = id, playing = playing)
	}

	private fun send(context: Context, action: String, id: String, path: String, video: Boolean) {
		val intent = Intent(context, MediaPlaybackService::class.java)
		intent.action = action
		intent.putExtra(MEDIA_ID, id)
		intent.putExtra(MEDIA_PATH, path)
		intent.putExtra(MEDIA_VIDEO, video)
		try {
			context.startForegroundService(intent)
		} catch (_: IllegalStateException) {
			state.value = MediaPhase(id = "", playing = false)
		} catch (_: SecurityException) {
			state.value = MediaPhase(id = "", playing = false)
		}
	}
}

internal fun String.video(): Boolean = substringBefore(";").lowercase().startsWith("video/")

internal const val PLAY = "media-play"
internal const val PAUSE = "media-pause"
internal const val RESUME = "media-resume"
internal const val MEDIA_ID = "media-id"
internal const val MEDIA_PATH = "media-path"
internal const val MEDIA_VIDEO = "media-video"
