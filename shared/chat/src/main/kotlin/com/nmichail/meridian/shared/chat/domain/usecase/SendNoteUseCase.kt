package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendNoteUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String, String, ChatNote) -> Unit by repository::sendNote