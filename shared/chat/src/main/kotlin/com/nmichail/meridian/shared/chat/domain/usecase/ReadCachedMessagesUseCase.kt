package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSource
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import javax.inject.Inject

class ReadCachedMessagesUseCase @Inject constructor(
	private val messageDataSource: MessageDataSource,
) {

	operator fun invoke(chatId: String): ChatThread? = messageDataSource.read(chatId)
}