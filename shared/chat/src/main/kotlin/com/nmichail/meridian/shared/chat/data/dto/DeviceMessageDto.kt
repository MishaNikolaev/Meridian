package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceMessageDto(
	@SerialName("recipientDeviceId")
	val deviceId: String,
	val body: String,
)
