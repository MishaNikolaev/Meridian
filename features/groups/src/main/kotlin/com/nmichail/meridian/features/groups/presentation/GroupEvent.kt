package com.nmichail.meridian.features.groups.presentation

sealed interface GroupEvent {

	data class Saved(
		val title: String,
		val description: String,
	) : GroupEvent
}