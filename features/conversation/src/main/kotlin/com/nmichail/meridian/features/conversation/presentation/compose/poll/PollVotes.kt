package com.nmichail.meridian.features.conversation.presentation.compose.poll

internal data class PollVotes(
	val counts: List<Int>,
	val mine: Int,
	val voters: List<PollVoter>,
)