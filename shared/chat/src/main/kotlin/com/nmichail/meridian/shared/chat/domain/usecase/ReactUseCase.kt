package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ReactUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String, String, Boolean) -> Unit by repository::react