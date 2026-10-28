package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class KeyDto(
	val userId: String,
	val devices: List<DeviceBundleDto>,
)
