package com.nmichail.meridian.navigation.router.ring

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.calls.ring.RingRouter
import com.nmichail.meridian.ui.call.getCallScreen
import javax.inject.Inject

class RingRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : RingRouter {

	override fun openIncoming(
		chatId: String,
		title: String,
		personId: String,
		callId: String,
		video: Boolean,
		accepted: Boolean,
	) {
		router.navigateTo(
			getCallScreen(
				chatId = chatId,
				title = title,
				personId = personId,
				incoming = true,
				callId = callId,
				video = video,
				accepted = accepted,
				onBack = { router.navigateBack() },
			),
		)
	}
}
