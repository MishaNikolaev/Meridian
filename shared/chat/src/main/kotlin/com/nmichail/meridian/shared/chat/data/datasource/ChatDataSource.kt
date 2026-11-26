package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.mapper.toChat
import com.nmichail.meridian.shared.chat.data.mapper.toRecord
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface ChatDataSource {
	suspend fun loadChats(): List<Chat>?

	suspend fun saveChats(chats: List<Chat>)
}

class ChatDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
	private val sessionRepository: SessionRepository,
) : ChatDataSource {
	private companion object {
		const val CHAT_STORE = "meridian_chats"
	}

	private val preferences = context.getSharedPreferences(CHAT_STORE, Context.MODE_PRIVATE)
	private val json = Json { ignoreUnknownKeys = true }

	override suspend fun loadChats(): List<Chat>? {
		val userId = sessionRepository.getUserId().orEmpty()
		val raw = preferences.getString("chats_$userId", null) ?: return null
		val page = try {
			json.decodeFromString<ChatRecords>(raw)
		} catch (_: SerializationException) {
			return null
		}

		return page.items.map { item -> item.toChat() ?: return null }
	}

	override suspend fun saveChats(chats: List<Chat>) {
		val userId = sessionRepository.getUserId().orEmpty()
		val raw = json.encodeToString(ChatRecords(items = chats.map { chat -> chat.toRecord() }))

		preferences.edit { putString("chats_$userId", raw) }
	}
}
