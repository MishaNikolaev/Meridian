package com.nmichail.meridian.features.profile.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyBoardDto(
	val people: List<BusyPersonDto>,
)