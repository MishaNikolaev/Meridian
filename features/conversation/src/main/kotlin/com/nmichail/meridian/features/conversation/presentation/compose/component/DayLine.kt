package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

data class DayLine(
	val line: ChatLine,
	val continued: Boolean,
)