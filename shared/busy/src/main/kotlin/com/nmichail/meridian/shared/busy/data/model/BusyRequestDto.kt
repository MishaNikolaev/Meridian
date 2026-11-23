package com.nmichail.meridian.shared.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyRequestDto(
	val day: String,
	val slots: List<Int>,
)
