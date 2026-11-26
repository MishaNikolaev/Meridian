package com.nmichail.meridian.features.calls.signal.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignalMessageDto(
	val senderDeviceId: String,
	val body: String,
)