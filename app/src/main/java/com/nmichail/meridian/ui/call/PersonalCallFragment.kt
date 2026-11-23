package com.nmichail.meridian.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.nmichail.meridian.MainActivity
import com.nmichail.meridian.MeridianApplication
import com.nmichail.meridian.component.photopicker.ensurePersonPhoto
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.calls.R
import com.nmichail.meridian.features.calls.di.CallsComponentHolder
import com.nmichail.meridian.features.calls.presentation.compose.media.CallLine
import com.nmichail.meridian.features.calls.presentation.compose.media.HearCall
import com.nmichail.meridian.features.calls.presentation.compose.media.rememberCallLine
import com.nmichail.meridian.features.calls.presentation.compose.ring.RingCall
import com.nmichail.meridian.features.calls.presentation.compose.ring.RingPhase
import com.nmichail.meridian.features.calls.ring.OutgoingRing
import com.nmichail.meridian.features.calls.ring.RingIncomingService
import com.nmichail.meridian.features.calls.ring.playCallEnd
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalEvent
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalIntent
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalViewModel
import com.nmichail.meridian.shared.chat.di.ChatRealtimeHolder
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
import com.nmichail.meridian.shared.phone.data.mapper.toCallPhase
import com.nmichail.meridian.shared.phone.domain.entity.CallMedia
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase

fun getCallScreen(
	chatId: String,
	title: String,
	personId: String,
	incoming: Boolean,
	callId: String,
	video: Boolean,
	onBack: () -> Unit,
	accepted: Boolean = false,
): FullScreen = object : FullScreen(
	key = "call-$chatId-$incoming-$video",
	fragmentClass = PersonalCallFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = PersonalCallFragment().apply {
		arguments = Bundle().apply {
			putString("chatId", chatId)
			putString("title", title)
			putString("personId", personId)
			putBoolean("incoming", incoming)
			putString("callId", callId)
			putBoolean("video", video)
			putBoolean("accepted", accepted)
		}
		back = onBack
	}
}

class PersonalCallFragment : FullScreenFragment() {
	var back: (() -> Unit)? = null

	private val signals: CallSignalViewModel by viewModels {
		CallsComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext())

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		(view as ComposeView).setContent {
			Screen {
				PersonalCall(
					chatId = requireArguments().getString("chatId").orEmpty(),
					title = requireArguments().getString("title").orEmpty(),
					personId = requireArguments().getString("personId").orEmpty(),
					incoming = requireArguments().getBoolean("incoming"),
					callId = requireArguments().getString("callId").orEmpty(),
					video = requireArguments().getBoolean("video"),
					accepted = requireArguments().getBoolean("accepted"),
					signals = signals,
					owner = viewLifecycleOwner,
					onClose = { navigateBack() },
				)
			}
		}
	}

	override fun navigateBack() {
		(activity as? MainActivity)?.ring()?.release()
		back?.invoke()
	}
}

private fun startPhase(incoming: Boolean, accepted: Boolean): RingPhase = when {
	accepted -> RingPhase.Live
	incoming -> RingPhase.Incoming
	else -> RingPhase.Outgoing
}

@Composable
private fun EnsureCallerFace(personId: String) {
	val context = LocalContext.current
	LaunchedEffect(personId) {
		ensurePersonPhoto(
			ktorfit = (context.applicationContext as MeridianApplication).component.ktorfit(),
			userId = personId,
		)
	}
}

@Composable
private fun PersonalCall(
	chatId: String,
	title: String,
	personId: String,
	incoming: Boolean,
	callId: String,
	video: Boolean,
	accepted: Boolean,
	signals: CallSignalViewModel,
	owner: LifecycleOwner,
	onClose: () -> Unit,
) {
	val context = LocalContext.current
	var phase by remember { mutableStateOf(startPhase(incoming, accepted)) }
	var current by remember { mutableStateOf(callId) }
	var closed by remember { mutableStateOf(false) }
	val realtime = ChatRealtimeHolder.get((context.applicationContext as MeridianApplication).component).chatRealtime()
	val finish = { tone: Boolean ->
		if (!closed && tone) {
			playCallEnd(context)
		}
		if (!closed) {
			closed = true
			onClose()
		}
	}
	val goLive = {
		RingIncomingService.stop(context, current)
		phase = RingPhase.Live
	}
	val request = { next: CallPhase ->
		signals.applyIntent(CallSignalIntent.Update(callId = current, phase = next))
	}
	val line = rememberCallLine(video = video, active = phase != RingPhase.Incoming)
	CallSession(
		title = title,
		personId = personId,
		incoming = incoming,
		video = video,
		phase = phase,
		current = current,
		chatId = chatId,
		line = line,
		signals = signals,
		realtime = realtime,
		owner = owner,
		onCurrent = { current = it },
		goLive = goLive,
		finish = finish,
		request = request,
	)
}

@Composable
private fun CallSession(
	title: String,
	personId: String,
	incoming: Boolean,
	video: Boolean,
	phase: RingPhase,
	current: String,
	chatId: String,
	line: CallLine?,
	signals: CallSignalViewModel,
	realtime: ChatRealtime,
	owner: LifecycleOwner,
	onCurrent: (String) -> Unit,
	goLive: () -> Unit,
	finish: (Boolean) -> Unit,
	request: (CallPhase) -> Unit,
) {
	val context = LocalContext.current
	EnsureCallerFace(personId)
	OutgoingRing(active = phase == RingPhase.Outgoing)
	BindRingHooks(callId = current, onLive = goLive, onClose = { finish(true) })
	WatchSignals(
		signals = signals,
		owner = owner,
		onPlaced = onCurrent,
		onAccepted = goLive,
		onClosed = {
			RingIncomingService.stop(context, current)
			finish(true)
		},
	)
	WatchCall(
		chatId = chatId,
		incoming = incoming,
		signals = signals,
		realtime = realtime,
		current = { current },
		onCurrent = { current = it },
		onLive = goLive,
		onDone = { finish(true) },
		video = video,
	)
	LiveCall(
		name = title.ifBlank { stringResource(R.string.calls_incoming) },
		personId = personId,
		phase = phase,
		video = video,
		line = line,
		incoming = incoming,
		callId = current,
		chatId = chatId,
		signals = signals,
		onAccept = { request(CallPhase.ACCEPTED) },
		onDecline = {
			request(if (incoming && phase != RingPhase.Live) CallPhase.DECLINED else CallPhase.ENDED)
		},
		onHangup = { request(CallPhase.ENDED) },
	)
}

@Composable
private fun WatchSignals(
	signals: CallSignalViewModel,
	owner: LifecycleOwner,
	onPlaced: (String) -> Unit,
	onAccepted: () -> Unit,
	onClosed: () -> Unit,
) {
	DisposableEffect(signals, owner) {
		val observer = Observer<CallSignalEvent> { event ->
			when (event) {
				is CallSignalEvent.Placed -> onPlaced(event.callId)
				is CallSignalEvent.Updated -> when (event.phase) {
					CallPhase.ACCEPTED -> onAccepted()
					CallPhase.DECLINED, CallPhase.ENDED -> onClosed()
					CallPhase.RINGING, CallPhase.MISSED -> Unit
				}
				CallSignalEvent.Offer -> Unit
			}
		}
		signals.event.observe(owner, observer)
		onDispose { signals.event.removeObserver(observer) }
	}
}

@Composable
private fun BindRingHooks(callId: String, onLive: () -> Unit, onClose: () -> Unit) {
	val rings = (LocalContext.current as MainActivity).ring()
	DisposableEffect(callId) {
		rings.attach(callId, onLive, onClose)
		onDispose { rings.detach(callId) }
	}
}

@Composable
private fun LiveCall(
	name: String,
	personId: String,
	phase: RingPhase,
	video: Boolean,
	line: CallLine?,
	incoming: Boolean,
	callId: String,
	chatId: String,
	signals: CallSignalViewModel,
	onAccept: () -> Unit,
	onDecline: () -> Unit,
	onHangup: () -> Unit,
) {
	HearCall(
		live = phase == RingPhase.Live,
		incoming = incoming,
		callId = callId,
		chatId = chatId,
		line = line,
		inbound = signals.inbound,
		onIntent = signals::applyIntent,
	)
	RingCall(
		name = name,
		userId = personId,
		phase = phase,
		video = video,
		line = line,
		onAccept = onAccept,
		onDecline = onDecline,
		onHangup = onHangup,
	)
}

@Composable
private fun WatchCall(
	chatId: String,
	incoming: Boolean,
	signals: CallSignalViewModel,
	realtime: ChatRealtime,
	current: () -> String,
	onCurrent: (String) -> Unit,
	onLive: () -> Unit,
	onDone: () -> Unit,
	video: Boolean,
) {
	LaunchedEffect(chatId) {
		if (chatId.isEmpty()) {
			onDone()
			return@LaunchedEffect
		}
		if (!incoming && current().isEmpty()) {
			val media = if (video) CallMedia.VIDEO else CallMedia.AUDIO
			signals.applyIntent(CallSignalIntent.Place(chatId = chatId, media = media))
		}
		realtime.calls.collect { notice ->
			if (notice.chatId != chatId || (current().isNotEmpty() && notice.callId != current())) {
				return@collect
			}
			onCurrent(notice.callId)
			when (notice.state.toCallPhase()) {
				CallPhase.ACCEPTED -> onLive()
				CallPhase.DECLINED, CallPhase.ENDED, CallPhase.MISSED -> onDone()
				CallPhase.RINGING, null -> Unit
			}
		}
	}
}