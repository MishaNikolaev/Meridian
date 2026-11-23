package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import javax.inject.Inject

class ReadReactionsUseCase @Inject constructor(
	private val repository: ReactionRepository,
) : () -> ReactionMemory by repository::readReactions