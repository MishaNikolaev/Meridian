package com.nmichail.meridian.features.calls.catalog.domain.entity

import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase

data class CallRecord(
	val chatId: String,
	val title: String,
	val personId: String,
	val initiatorUserId: String,
	val chatType: ChatType,
	val phase: CallPhase?,
	val startedAt: String,
	val endedAt: String?,
)