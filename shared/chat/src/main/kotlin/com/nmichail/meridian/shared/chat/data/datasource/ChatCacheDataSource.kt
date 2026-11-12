package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.mapper.toCached
import com.nmichail.meridian.shared.chat.data.mapper.toChat
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface ChatCacheDataSource {
	suspend fun read(): List<Chat>?

	suspend fun save(chats: List<Chat>)
}

class ChatCacheDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
	private val sessionRepository: SessionRepository,
) : ChatCacheDataSource {
	private companion object {
		const val CHAT_STORE = "meridian_chats"
	}

	private val preferences = context.getSharedPreferences(CHAT_STORE, Context.MODE_PRIVATE)

	private val json = Json { ignoreUnknownKeys = true }

	override suspend fun read(): List<Chat>? {
		val raw = preferences.getString(key(userId()), null) ?: return null

		val page = try {
			json.decodeFromString<CachedChatPage>(raw)
		} catch (_: SerializationException) {
			return null
		}

		return page.items.map { item -> item.toChat() ?: return null }
	}

	override suspend fun save(chats: List<Chat>) {
		val raw = json.encodeToString(CachedChatPage(items = chats.map { chat -> chat.toCached() }))

		preferences.edit { putString(key(userId()), raw) }
	}

	private fun userId(): String = sessionRepository.getUserId().orEmpty()

	private fun key(userId: String): String = "chats_$userId"
}