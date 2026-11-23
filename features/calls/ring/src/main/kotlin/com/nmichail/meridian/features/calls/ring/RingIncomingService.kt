package com.nmichail.meridian.features.calls.ring

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.Person

class RingIncomingService : Service() {
	companion object {
		const val SHOW = "meridian.ring.show"
		const val STOP = "meridian.ring.stop"
		const val ANSWER = "meridian.ring.answer"
		const val DECLINE = "meridian.ring.decline"
		const val OPEN = "meridian.ring.open"
		const val CALL_ID = "callId"
		const val CHAT_ID = "chatId"
		const val TITLE = "title"
		const val PERSON_ID = "personId"
		const val VIDEO = "video"
		private const val CHANNEL = "ring_incoming_v2"
		private const val NOTE = 71
		private const val CODE_ANSWER = 1
		private const val CODE_DECLINE = 2
		private const val CODE_OPEN = 3
		private val PULSE = longArrayOf(0L, 800L, 400L, 800L)

		fun show(context: Context, banner: RingBanner) {
			val intent = Intent(context, RingIncomingService::class.java)
				.setAction(SHOW)
				.putExtra(CALL_ID, banner.callId)
				.putExtra(CHAT_ID, banner.chatId)
				.putExtra(TITLE, banner.title)
				.putExtra(PERSON_ID, banner.personId)
				.putExtra(VIDEO, banner.video)
			context.startForegroundService(intent)
		}

		fun stop(context: Context, callId: String) {
			context.startService(
				Intent(context, RingIncomingService::class.java)
					.setAction(STOP)
					.putExtra(CALL_ID, callId),
			)
		}
	}

	private var sound: RingSound? = null
	private var active: RingBanner? = null

	override fun onBind(intent: Intent?): IBinder? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			SHOW -> show(intent.banner())
			STOP -> {
				if (active?.callId == intent.getStringExtra(CALL_ID)) {
					finish()
				}
			}
			ANSWER, DECLINE -> {
				forward(intent.action.orEmpty(), intent.banner())
				finish()
			}
			else -> finish()
		}
		return START_NOT_STICKY
	}

	override fun onDestroy() {
		sound?.stop()
		sound = null
		active = null
		super.onDestroy()
	}

	private fun show(banner: RingBanner?) {
		if (banner == null || banner.callId.isEmpty()) {
			finish()
			return
		}
		active = banner
		val ring = sound ?: RingSound(this).also { sound = it }
		ring.play(incoming = true)
		val note = notice(banner)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			startForeground(NOTE, note, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
		} else {
			startForeground(NOTE, note)
		}
	}

	private fun forward(action: String, banner: RingBanner?) {
		val open = banner?.let { ringIntent(action, it) } ?: return
		startActivity(open)
	}

	private fun finish() {
		sound?.stop()
		sound = null
		active = null
		stopForeground(STOP_FOREGROUND_REMOVE)
		stopSelf()
	}

	private fun notice(banner: RingBanner): Notification {
		val manager = getSystemService(NotificationManager::class.java)
		val channel = NotificationChannel(CHANNEL, getString(R.string.ring_channel), NotificationManager.IMPORTANCE_HIGH)
		channel.setSound(null, null)
		channel.enableVibration(true)
		channel.vibrationPattern = PULSE
		channel.setBypassDnd(true)
		channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
		manager.createNotificationChannel(channel)
		val name = banner.title.ifBlank { getString(R.string.ring_incoming) }
		val person = Person.Builder().setName(name).setImportant(true).build()
		val answer = action(ANSWER, banner, CODE_ANSWER)
		val decline = action(DECLINE, banner, CODE_DECLINE)
		val open = PendingIntent.getActivity(
			this,
			CODE_OPEN,
			ringIntent(OPEN, banner),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)
		return NotificationCompat.Builder(this, CHANNEL)
			.setSmallIcon(R.drawable.ic_ring_in)
			.setContentTitle(name)
			.setContentText(getString(R.string.ring_heads_up))
			.setCategory(NotificationCompat.CATEGORY_CALL)
			.setPriority(NotificationCompat.PRIORITY_MAX)
			.setOngoing(true)
			.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			.setContentIntent(open)
			.setFullScreenIntent(open, true)
			.setStyle(NotificationCompat.CallStyle.forIncomingCall(person, decline, answer))
			.build()
	}

	private fun action(kind: String, banner: RingBanner, code: Int): PendingIntent =
		PendingIntent.getService(
			this,
			code,
			Intent(this, RingIncomingService::class.java)
				.setAction(kind)
				.putExtra(CALL_ID, banner.callId)
				.putExtra(CHAT_ID, banner.chatId)
				.putExtra(TITLE, banner.title)
				.putExtra(PERSON_ID, banner.personId)
				.putExtra(VIDEO, banner.video),
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)

	private fun Intent.banner(): RingBanner? {
		val callId = getStringExtra(CALL_ID).orEmpty()
		if (callId.isEmpty()) {
			return null
		}
		return RingBanner(
			callId = callId,
			chatId = getStringExtra(CHAT_ID).orEmpty(),
			title = getStringExtra(TITLE).orEmpty(),
			personId = getStringExtra(PERSON_ID).orEmpty(),
			video = getBooleanExtra(VIDEO, false),
		)
	}
}

private fun Context.ringIntent(action: String, banner: RingBanner): Intent {
	val launch = checkNotNull(packageManager.getLaunchIntentForPackage(packageName))
	return launch
		.setAction(action)
		.putExtra(RingIncomingService.CALL_ID, banner.callId)
		.putExtra(RingIncomingService.CHAT_ID, banner.chatId)
		.putExtra(RingIncomingService.TITLE, banner.title)
		.putExtra(RingIncomingService.PERSON_ID, banner.personId)
		.putExtra(RingIncomingService.VIDEO, banner.video)
		.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
