package com.nmichail.meridian.features.calls.ring

data class RingBanner(
	val callId: String,
	val chatId: String,
	val title: String,
	val personId: String,
	val video: Boolean,
)
