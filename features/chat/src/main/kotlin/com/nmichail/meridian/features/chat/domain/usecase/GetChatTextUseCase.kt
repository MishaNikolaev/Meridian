package com.nmichail.meridian.features.chat.domain.usecase

import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatTextUseCase @Inject constructor(
	private val repository: ChatRepository,
) : () -> String by repository::getText
