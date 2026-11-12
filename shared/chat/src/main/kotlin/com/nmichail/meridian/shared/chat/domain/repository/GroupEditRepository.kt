package com.nmichail.meridian.shared.chat.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile

interface GroupEditRepository {
	suspend fun readChat(chatId: String): ChatProfile

	suspend fun updateChat(chatId: String, title: String, description: String): ChatProfile

	suspend fun addChat(chatId: String, userId: String)

	suspend fun removeChat(chatId: String, userId: String)

	suspend fun transferChat(chatId: String, userId: String)

	suspend fun promoteChat(chatId: String, userId: String)

	suspend fun leaveChat(chatId: String)
}