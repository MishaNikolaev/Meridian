package com.nmichail.meridian.features.calls.signal.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignalRequestDto(
	val devices: List<SignalDeviceDto>,
)