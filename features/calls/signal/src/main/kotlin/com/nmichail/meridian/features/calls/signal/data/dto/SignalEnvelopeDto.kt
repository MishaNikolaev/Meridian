package com.nmichail.meridian.features.calls.signal.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignalEnvelopeDto(
	val senderDeviceId: String,
	val body: String,
)
