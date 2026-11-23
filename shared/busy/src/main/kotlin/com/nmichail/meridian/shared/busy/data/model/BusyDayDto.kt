package com.nmichail.meridian.shared.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyDayDto(
	val day: String,
	val slots: List<Int>,
)
