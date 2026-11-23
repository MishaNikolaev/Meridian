package com.nmichail.meridian.features.profile.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyPersonDto(
	val userId: String,
	val name: String,
	val slots: List<Int>,
)