package com.nmichail.meridian.shared.chat.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat

interface ChatRepository {

	suspend fun loadChats(): List<ChatSummary>

	suspend fun create(type: ChatType, title: String, username: String): CreatedChat

	suspend fun loadMessages(chatId: String): ChatThread

	suspend fun send(chatId: String, text: String)

	suspend fun markRead(messageId: String)
}
