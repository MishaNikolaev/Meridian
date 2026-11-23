package com.nmichail.meridian.component.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException

private const val CHANNEL = "playback"
private const val NOTE = 42
private const val PAUSE_CODE = 1
private const val RESUME_CODE = 2

class MediaPlaybackService : Service() {
	private var player: MediaPlayer? = null
	private var focus: AudioFocusRequest? = null
	private var clip = ""
	private var video = false

	override fun onBind(intent: Intent?): IBinder? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			PLAY -> play(
				id = intent.getStringExtra(MEDIA_ID).orEmpty(),
				path = intent.getStringExtra(MEDIA_PATH).orEmpty(),
				video = intent.getBooleanExtra(MEDIA_VIDEO, false),
			)
			PAUSE -> pause()
			RESUME -> resume()
			else -> play(id = "", path = "", video = false)
		}
		return START_NOT_STICKY
	}

	override fun onDestroy() {
		release()
		MediaPlayback.setPhase(id = "", playing = false)
		super.onDestroy()
	}

	private fun play(id: String, path: String, video: Boolean) {
		fun fail(next: MediaPlayer) {
			next.release()
			MediaPlayback.setPhase(id = "", playing = false)
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
		}
		if (id.isEmpty() || path.isEmpty()) {
			MediaPlayback.setPhase(id = "", playing = false)
			foreground(playing = false)
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
			return
		}

		release()
		this.video = video
		foreground(playing = true)
		val next = MediaPlayer()
		var file: FileInputStream? = null
		try {
			file = FileInputStream(File(path))
			next.setAudioAttributes(if (video) movie() else speech())
			next.setDataSource(file.fd)
			next.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
			next.setOnCompletionListener {
				MediaPlayback.setPhase(id = "", playing = false)
				release()
				stopForeground(STOP_FOREGROUND_REMOVE)
				stopSelf()
			}
			next.prepare()
			listen()
			next.start()
			player = next
			clip = id
			MediaPlayback.setPhase(id = id, playing = true)
		} catch (e: Exception) {
			when (e) {
				is IOException, is IllegalStateException, is IllegalArgumentException -> fail(next)
				else -> throw e
			}
		} finally {
			file?.close()
		}
	}

	private fun pause() {
		foreground(playing = false)
		val current = player ?: return
		val going = try {
			current.isPlaying
		} catch (_: IllegalStateException) {
			false
		}
		if (going) {
			current.pause()
		}
		MediaPlayback.setPhase(id = clip, playing = false)
	}

	private fun resume() {
		foreground(playing = true)
		val current = player ?: return
		listen()
		current.start()
		MediaPlayback.setPhase(id = clip, playing = true)
	}

	private fun listen() {
		val manager = getSystemService(AudioManager::class.java)
		val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(track()).build()
		focus = request
		manager.requestAudioFocus(request)
	}

	private fun track(): AudioAttributes = if (video) movie() else speech()

	private fun release() {
		player?.release()
		player = null
		val manager = getSystemService(AudioManager::class.java)
		focus?.let { request -> manager.abandonAudioFocusRequest(request) }
		focus = null
	}

	private fun foreground(playing: Boolean) {
		val manager = getSystemService(NotificationManager::class.java)
		val channel = NotificationChannel(CHANNEL, getString(R.string.playback_title), NotificationManager.IMPORTANCE_LOW)
		manager.createNotificationChannel(channel)
		val action = if (playing) PAUSE else RESUME
		val label = getString(if (playing) R.string.playback_pause else R.string.playback_play)
		val code = if (playing) PAUSE_CODE else RESUME_CODE
		val pending = PendingIntent.getService(
			this,
			code,
			Intent(this, MediaPlaybackService::class.java).setAction(action),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)
		val note = Notification.Builder(this, CHANNEL)
			.setSmallIcon(R.drawable.ic_playback_status)
			.setContentTitle(getString(R.string.playback_title))
			.setOngoing(true)
			.addAction(Notification.Action.Builder(null, label, pending).build())
			.build()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			startForeground(NOTE, note, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
		} else {
			startForeground(NOTE, note)
		}
	}
}

private fun speech(): AudioAttributes = AudioAttributes.Builder()
	.setUsage(AudioAttributes.USAGE_MEDIA)
	.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
	.build()

private fun movie(): AudioAttributes = AudioAttributes.Builder()
	.setUsage(AudioAttributes.USAGE_MEDIA)
	.setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
	.build()
