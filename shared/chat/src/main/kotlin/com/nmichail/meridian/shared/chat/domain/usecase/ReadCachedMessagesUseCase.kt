package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.memory.MessageMemory
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import javax.inject.Inject

class ReadCachedMessagesUseCase @Inject constructor(
	private val memory: MessageMemory,
) {

	operator fun invoke(chatId: String): ChatThread? = memory.threads[chatId]
}