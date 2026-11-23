package com.nmichail.meridian.shared.chat.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat

interface ChatRepository {
	suspend fun loadChats(): List<Chat>

	suspend fun readChat(chatId: String): Chat

	suspend fun create(type: ChatType, title: String, usernames: List<String>): CreatedChat

	suspend fun findPerson(username: String): ChatPerson

	suspend fun loadMessages(chatId: String): ChatThread

	suspend fun sendNote(chatId: String, threadRoot: String, note: ChatNote)

	suspend fun sendMessage(
		chatId: String,
		text: String,
		replyAuthor: String,
		replyText: String,
		threadRoot: String,
		fileId: String,
		fileName: String,
		fileType: String,
		photoId: List<String>,
	)

	suspend fun markRead(messageId: String)

	suspend fun delete(messageId: String)
}
