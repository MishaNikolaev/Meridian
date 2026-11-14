package com.nmichail.meridian.shared.chat.data.datasource

import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

interface MessageDataSource {

	fun read(chatId: String): ChatThread?

	fun save(chatId: String, thread: ChatThread)
}

class MessageDataSourceImpl @Inject constructor() : MessageDataSource {

	private val threads = ConcurrentHashMap<String, ChatThread>()

	override fun read(chatId: String): ChatThread? = threads[chatId]

	override fun save(chatId: String, thread: ChatThread) {
		threads[chatId] = thread
	}
}
