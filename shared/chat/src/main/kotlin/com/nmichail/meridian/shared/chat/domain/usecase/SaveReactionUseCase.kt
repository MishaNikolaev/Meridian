package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SaveReactionUseCase @Inject constructor(
	private val repository: ChatRepository,
) : (String, Set<String>) -> Unit {
	override fun invoke(messageId: String, reactions: Set<String>) {
		repository.saveReaction(
			messageId = messageId,
			reactions = reactions,
			saveUsed = true,
		)
	}
}