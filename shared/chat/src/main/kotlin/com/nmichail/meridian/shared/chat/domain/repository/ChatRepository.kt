package com.nmichail.meridian.shared.chat.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory

interface ChatRepository {
	suspend fun loadChats(): List<Chat>

	suspend fun create(type: ChatType, title: String, usernames: List<String>): CreatedChat

	suspend fun findPerson(username: String): ChatPerson

	suspend fun loadMessages(chatId: String): ChatThread

	suspend fun send(chatId: String, text: String, replyAuthor: String, replyText: String, threadRoot: String)

	suspend fun markRead(messageId: String)

	suspend fun delete(messageId: String)

	fun readReactions(): ReactionMemory

	fun saveReaction(messageId: String, reactions: Set<String>, saveUsed: Boolean)

	suspend fun react(messageId: String, reaction: String, active: Boolean)
}
