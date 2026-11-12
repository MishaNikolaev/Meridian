package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ReadReactionsUseCase @Inject constructor(
	private val repository: ChatRepository,
) : () -> ReactionMemory by repository::readReactions