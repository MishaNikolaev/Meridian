package com.nmichail.meridian.features.calls.signal.presentation

import com.nmichail.meridian.shared.phone.domain.entity.CallMedia
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase

sealed interface CallSignalIntent {

	data class Place(
		val chatId: String,
		val media: CallMedia,
	) : CallSignalIntent

	data class Update(
		val callId: String,
		val phase: CallPhase,
	) : CallSignalIntent

	data class Start(
		val callId: String,
		val chatId: String,
		val incoming: Boolean,
	) : CallSignalIntent

	data object Stop : CallSignalIntent

	data class Outgoing(val body: String) : CallSignalIntent
}