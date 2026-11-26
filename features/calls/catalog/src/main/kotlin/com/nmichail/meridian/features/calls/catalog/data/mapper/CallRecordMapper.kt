package com.nmichail.meridian.features.calls.catalog.data.mapper

import com.nmichail.meridian.features.calls.catalog.data.model.CallHistoryItemDto
import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.phone.data.mapper.toCallPhase

internal fun CallHistoryItemDto.toRecord(chat: Chat): CallRecord = CallRecord(
	chatId = chatId,
	title = chat.title,
	personId = chat.personId.orEmpty(),
	initiatorUserId = initiatorUserId,
	chatType = chat.type,
	phase = state.toCallPhase(),
	startedAt = startedAt,
	endedAt = endedAt,
)
