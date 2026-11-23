package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ReadChatUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String) -> Chat by repository::readChat