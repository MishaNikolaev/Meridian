package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String, String, String, String, String, String, String, String, List<String>) -> Unit by repository::sendMessage