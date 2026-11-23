package com.nmichail.meridian.features.groups.info.presentation

sealed interface GroupInfoEvent {
	data class Opened(
		val name: String,
		val type: String,
		val bytes: ByteArray,
	) : GroupInfoEvent

	data class Link(val link: String) : GroupInfoEvent
}