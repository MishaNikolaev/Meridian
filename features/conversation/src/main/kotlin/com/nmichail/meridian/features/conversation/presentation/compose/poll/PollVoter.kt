package com.nmichail.meridian.features.conversation.presentation.compose.poll

internal data class PollVoter(
	val option: Int,
	val name: String,
	val userId: String,
)