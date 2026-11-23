package com.nmichail.meridian.shared.busy.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusyBoardDto(
	val people: List<BusyPersonDto>,
)
