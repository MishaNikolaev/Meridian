package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

interface MessageDataSource {
	fun loadThread(chatId: String): ChatThread?

	fun saveThread(chatId: String, thread: ChatThread)

	fun loadDeleted(): Set<String>

	fun saveDeleted(messageId: String) {
		saveDeleted(setOf(messageId))
	}

	fun saveDeleted(messageIds: Collection<String>)
}

class MessageDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : MessageDataSource {
	private companion object {
		const val PREFERENCES = "meridian"
		const val DELETED_MESSAGES = "deleted_messages"
	}

	private val saved = ConcurrentHashMap<String, ChatThread>()
	private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

	override fun loadThread(chatId: String): ChatThread? = saved[chatId]

	override fun saveThread(chatId: String, thread: ChatThread) {
		saved[chatId] = thread
	}

	override fun loadDeleted(): Set<String> {
		val deleted = try {
			preferences.getStringSet(DELETED_MESSAGES, null)
		} catch (_: ClassCastException) {
			null
		}

		if (deleted != null) {
			return deleted.toSet()
		}

		val text = preferences.getString(DELETED_MESSAGES, null)
			.orEmpty()
			.split(",")
			.filter { messageId -> messageId.isNotEmpty() }
			.toSet()

		if (text.isNotEmpty()) {
			preferences.edit { putStringSet(DELETED_MESSAGES, text) }
		}

		return text
	}

	override fun saveDeleted(messageIds: Collection<String>) {
		if (messageIds.isEmpty()) {
			return
		}

		val stored = loadDeleted() + messageIds

		preferences.edit { putStringSet(DELETED_MESSAGES, stored) }
	}
}
