package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.ChatSummaryDto
import com.nmichail.meridian.shared.chat.data.dto.ChatTypeDto
import com.nmichail.meridian.shared.chat.data.mention.namedMention

internal suspend fun ChatSummaryDto.members(
	api: ChatApi,
	self: String?,
	text: String,
): List<ChatMemberDto> {
	val group = type != ChatTypeDto.DIRECT
	val needsUsername = group && unreadCount > 0 && self != null && namedMention(text)
	if (title != null && !needsUsername) {
		return emptyList()
	}
	return api.getChat(chatId).members
}
