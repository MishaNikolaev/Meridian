package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class LoadChatsUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend () -> List<Chat> by repository::loadChats