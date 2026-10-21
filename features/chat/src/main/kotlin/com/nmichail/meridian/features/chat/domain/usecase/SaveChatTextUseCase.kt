package com.nmichail.meridian.features.chat.domain.usecase

import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SaveChatTextUseCase @Inject constructor(
	private val repository: ChatRepository,
) : (String) -> Unit by repository::save
