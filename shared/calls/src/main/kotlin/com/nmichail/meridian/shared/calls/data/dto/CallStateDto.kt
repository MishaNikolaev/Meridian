package com.nmichail.meridian.shared.calls.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CallStateDto(
	val state: String,
)
