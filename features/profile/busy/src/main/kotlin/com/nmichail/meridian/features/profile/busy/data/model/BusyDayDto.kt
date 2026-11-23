package com.nmichail.meridian.features.profile.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyDayDto(
	val day: String,
	val slots: List<Int>,
)