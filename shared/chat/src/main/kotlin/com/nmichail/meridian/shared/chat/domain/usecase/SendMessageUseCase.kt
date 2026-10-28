package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String, String, String, String, String) -> Unit by repository::send