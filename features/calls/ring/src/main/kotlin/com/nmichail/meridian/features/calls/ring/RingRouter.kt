package com.nmichail.meridian.features.calls.ring

interface RingRouter {

	fun openIncoming(
		chatId: String,
		title: String,
		personId: String,
		callId: String,
		video: Boolean,
		accepted: Boolean,
	)
}
