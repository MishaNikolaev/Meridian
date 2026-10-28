package com.nmichail.meridian.shared.chat.data.chat

import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat

internal fun ChatDetailsDto.toCreatedChat(self: String?): CreatedChat =
	CreatedChat(
		id = chat.chatId,
		title = chat.title ?: personName(
			self = self,
			members = members,
			personId = chat.personId,
		),
		corporate = chat.corporate,
	)