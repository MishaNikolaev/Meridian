package com.nmichail.meridian.shared.group.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.entity.JoinedChat

interface GroupRepository {
	suspend fun readChat(chatId: String): ChatProfile

	suspend fun searchPeople(query: String): List<ChatPerson>

	suspend fun updateChat(chatId: String, title: String, description: String, photoId: String): ChatProfile

	suspend fun addChat(chatId: String, userId: String)

	suspend fun removeChat(chatId: String, userId: String)

	suspend fun transferChat(chatId: String, userId: String)

	suspend fun promoteChat(chatId: String, userId: String)

	suspend fun leaveChat(chatId: String)

	suspend fun createInvite(chatId: String): String

	suspend fun acceptInvite(code: String): JoinedChat
}