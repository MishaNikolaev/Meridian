package com.nmichail.meridian.shared.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyPersonDto(
	val userId: String,
	val name: String,
	val slots: List<Int>,
)
