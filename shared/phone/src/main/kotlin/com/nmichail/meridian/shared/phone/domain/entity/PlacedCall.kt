package com.nmichail.meridian.shared.phone.domain.entity

data class PlacedCall(
	val callId: String,
	val chatId: String,
	val initiatorUserId: String,
	val state: CallPhase,
)