package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (ChatType, String, List<String>) -> CreatedChat by repository::create