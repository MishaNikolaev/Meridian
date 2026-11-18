package com.nmichail.meridian.features.conversation.presentation.compose.component

internal data class PollSwitch(
	val icon: Int,
	val tint: Long,
	val title: Int,
	val checked: Boolean,
	val flip: (PollDraft) -> PollDraft,
)