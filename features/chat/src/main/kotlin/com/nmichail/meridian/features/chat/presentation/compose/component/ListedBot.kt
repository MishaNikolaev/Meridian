package com.nmichail.meridian.features.chat.presentation.compose.component

internal data class ListedBot(
	val id: String,
	val title: Int,
	val preview: Int,
	val initials: Int,
	val time: Int?,
	val unread: Int,
)