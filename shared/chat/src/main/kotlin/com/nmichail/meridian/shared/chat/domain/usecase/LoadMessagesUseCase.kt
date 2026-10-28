package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.memory.MessageMemory
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class LoadMessagesUseCase @Inject constructor(
	private val repository: ChatRepository,
	private val memory: MessageMemory,
) {

	suspend operator fun invoke(chatId: String): ChatThread {
		val thread = repository.loadMessages(chatId)
		memory.threads[chatId] = thread
		return thread
	}
}
