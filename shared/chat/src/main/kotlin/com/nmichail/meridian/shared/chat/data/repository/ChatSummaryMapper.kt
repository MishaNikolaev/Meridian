package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.model.ChatSummaryDto
import com.nmichail.meridian.shared.chat.data.model.MessageDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary

internal suspend fun ChatSummaryDto.toSummary(api: ChatApi, self: String?): ChatSummary {
	val name = title ?: peerTitle(api, self, chatId, peerUserId)
	val text = lastMessage?.getText().orEmpty()
	return ChatSummary(
		id = chatId,
		type = type.toEntity(),
		title = name,
		preview = text,
		sentAt = lastMessage?.createdAt,
		unread = unreadCount,
	)
}

internal fun MessageDto.toLine(self: String?, author: String? = null): ChatLine = ChatLine(
	id = messageId,
	mine = senderUserId == self,
	text = getText(),
	sentAt = createdAt,
	author = author?.takeIf { senderUserId != self },
)

private suspend fun peerTitle(api: ChatApi, self: String?, chatId: String, peerUserId: String?): String {
	val details = api.getChat(chatId)
	return peerName(self, details.members.map { it.userId to it.displayName }, peerUserId)
}

internal fun peerName(self: String?, members: List<Pair<String, String>>, peerUserId: String?): String {
	val peer = members.firstOrNull { it.first == peerUserId } ?: members.firstOrNull { it.first != self }
	return peer?.second ?: "Чат"
}

private fun MessageDto.getText(): String {
	if (deleted) {
		return ""
	}
	val body = envelopes.firstOrNull()?.body ?: return ""
	return MessageCipher.open(chatId, body)
}
