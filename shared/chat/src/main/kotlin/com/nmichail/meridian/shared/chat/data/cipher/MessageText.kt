package com.nmichail.meridian.shared.chat.data.cipher

import com.nmichail.meridian.shared.chat.data.dto.MessageDto

internal fun MessageDto.opened(): OpenedMessage? {
	if (deleted) {
		return null
	}

	val encrypted = deviceMessages.firstOrNull()?.body ?: return null
	return decodeOpenedMessage(
		MessageCipher.decrypt(
			chatId = chatId,
			encrypted = encrypted,
		),
	)
}