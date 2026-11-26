package com.nmichail.meridian.features.calls.signal.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignalDeviceDto(
	@SerialName("recipientDeviceId")
	val deviceId: String,
	val body: String,
)