package com.nmichail.meridian.features.groups.presentation

sealed interface GroupInfoEvent {
	data class Opened(
		val name: String,
		val type: String,
		val bytes: ByteArray,
	) : GroupInfoEvent
}