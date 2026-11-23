package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import javax.inject.Inject

class SaveReactionUseCase @Inject constructor(
	private val repository: ReactionRepository,
) : (String, Set<String>) -> Unit {
	override fun invoke(messageId: String, reactions: Set<String>) {
		repository.saveReaction(
			messageId = messageId,
			reactions = reactions,
			saveUsed = true,
		)
	}
}