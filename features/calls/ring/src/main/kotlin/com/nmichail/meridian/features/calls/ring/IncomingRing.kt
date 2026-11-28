package com.nmichail.meridian.features.calls.ring

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.loadPersonFace
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.RingNotice
import com.nmichail.meridian.shared.calls.data.mapper.toCallMedia
import com.nmichail.meridian.shared.calls.data.mapper.toCallPhase
import com.nmichail.meridian.shared.calls.domain.entity.CallMedia
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
import de.jensklingenberg.ktorfit.Ktorfit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class IncomingRing @Inject constructor(
	private val activity: Activity,
	private val owner: LifecycleOwner,
	private val router: RingRouter,
	private val getUserIdUseCase: GetUserIdUseCase,
	private val session: RingSession,
	private val ktorfit: Ktorfit,
	private val errorDelegate: ErrorDelegate,
) {
	private var banner: RingBanner? = null
	private var shownCallId: String? = null
	private var onLive: (() -> Unit)? = null
	private var onClose: (() -> Unit)? = null

	fun attach(callId: String, onLive: () -> Unit, onClose: () -> Unit) {
		shownCallId = callId
		this.onLive = onLive
		this.onClose = onClose
	}

	fun detach(callId: String) {
		if (shownCallId == callId) {
			shownCallId = null
			onLive = null
			onClose = null
		}
	}

	suspend fun onNotice(notice: RingNotice) {
		when (notice.state.toCallPhase()) {
			CallPhase.RINGING -> show(notice)
			CallPhase.DECLINED, CallPhase.ENDED, CallPhase.MISSED, CallPhase.ACCEPTED -> hide(notice.callId)
			null -> Unit
		}
	}

	fun onIntent(intent: Intent?) {
		val action = intent?.action ?: return
		val next = intent.ringBanner() ?: return
		when (action) {
			RingIncomingService.ANSWER -> answer(next)
			RingIncomingService.DECLINE -> decline(next)
			RingIncomingService.OPEN -> open(next, accepted = false)
		}
	}

	private suspend fun show(notice: RingNotice) {
		val currentUserId = getUserIdUseCase().orEmpty()
		if (notice.actorUserId.isEmpty() || notice.actorUserId == currentUserId) {
			return
		}
		if (banner?.callId == notice.callId || shownCallId == notice.callId) {
			return
		}
		val chat = try {
			withTimeoutOrNull(1_500.milliseconds) { session.readChat(notice.chatId) }
		} catch (error: CancellationException) {
			throw error
		} catch (error: Exception) {
			errorDelegate.processNonBlockingError(error)
			null
		}
		val personId = when (chat?.type) {
			ChatType.DIRECT -> chat.personId?.takeIf(String::isNotBlank) ?: notice.actorUserId
			null -> notice.actorUserId
			else -> ""
		}
		val next = RingBanner(
			callId = notice.callId,
			chatId = notice.chatId,
			title = chat?.title.orEmpty(),
			personId = personId,
			video = notice.media.toCallMedia() == CallMedia.VIDEO,
		)
		banner = next
		val saved = if (personId.isEmpty()) {
			PhotoLocalDataSource.storedChatPhoto(notice.chatId)
		} else {
			PhotoLocalDataSource.storedPersonPhoto(personId)
		}
		RingIncomingService.show(activity, next, saved)
		if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
			open(next, accepted = false)
		}
		loadCallerPhoto(next, saved)
	}

	private fun loadCallerPhoto(current: RingBanner, saved: String) {
		if (current.personId.isEmpty()) {
			return
		}
		owner.lifecycleScope.launch {
			val face = loadPersonFace(ktorfit = ktorfit, userId = current.personId)
			val titled = current.copy(title = current.title.ifBlank { face.name })
			if (banner?.callId == titled.callId && (titled != current || face.photoId != saved)) {
				banner = titled
				RingIncomingService.show(activity, titled, face.photoId)
			}
		}
	}

	fun hide(callId: String) {
		if (banner?.callId == callId) {
			banner = null
		}
		RingIncomingService.stop(activity, callId)
	}

	private fun decline(current: RingBanner) {
		owner.lifecycleScope.launch {
			if (!session.report(current.callId, CallPhase.DECLINED, errorDelegate)) {
				return@launch
			}
			hide(current.callId)
			onClose?.invoke()
		}
	}

	private fun answer(current: RingBanner) {
		owner.lifecycleScope.launch {
			if (!session.report(current.callId, CallPhase.ACCEPTED, errorDelegate)) {
				return@launch
			}
			hide(current.callId)
			if (shownCallId == current.callId) {
				onLive?.invoke()
			} else {
				open(current, accepted = true)
			}
		}
	}

	private fun open(current: RingBanner, accepted: Boolean) {
		if (shownCallId == current.callId) {
			if (accepted) {
				onLive?.invoke()
			}
			return
		}
		shownCallId = current.callId
		router.openIncoming(
			chatId = current.chatId,
			title = current.title,
			personId = current.personId,
			callId = current.callId,
			video = current.video,
			accepted = accepted,
		)
	}
}

private suspend fun RingSession.report(
	callId: String,
	phase: CallPhase,
	errorDelegate: ErrorDelegate,
): Boolean = try {
	updateCall(callId, phase)
	true
} catch (error: CancellationException) {
	throw error
} catch (error: Exception) {
	errorDelegate.processNonBlockingError(error)
	false
}

private fun Intent.ringBanner(): RingBanner? {
	val callId = getStringExtra(RingIncomingService.CALL_ID).orEmpty()
	if (callId.isEmpty()) {
		return null
	}
	return RingBanner(
		callId = callId,
		chatId = getStringExtra(RingIncomingService.CHAT_ID).orEmpty(),
		title = getStringExtra(RingIncomingService.TITLE).orEmpty(),
		personId = getStringExtra(RingIncomingService.PERSON_ID).orEmpty(),
		video = getBooleanExtra(RingIncomingService.VIDEO, false),
	)
}