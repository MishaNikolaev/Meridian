package com.nmichail.meridian.shared.chat.domain.entity

sealed interface ChatNote {
	data class PollBody(val poll: Poll) : ChatNote

	data class VoteBody(val vote: Vote) : ChatNote
}