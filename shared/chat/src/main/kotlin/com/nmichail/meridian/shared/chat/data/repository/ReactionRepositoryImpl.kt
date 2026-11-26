package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import javax.inject.Inject

class ReactionRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val reactionDataSource: ReactionDataSource,
) : ReactionRepository {
	override fun readReactions(): ReactionMemory = ReactionMemory(
		chosen = reactionDataSource.loadReactions(),
		used = reactionDataSource.loadUsed(),
	)

	override fun saveReaction(messageId: String, reactions: Set<String>, saveUsed: Boolean) {
		val stored = reactionDataSource.loadReactions()[messageId].orEmpty()

		reactionDataSource.saveReactions(messageId, reactions)

		if (saveUsed) {
			(reactions - stored).forEach(reactionDataSource::saveUsed)
		}
	}

	override suspend fun react(messageId: String, reaction: String, active: Boolean) {
		api.setReaction(
			messageId,
			ReactionRequestDto(
				reaction = reaction,
				active = active,
			),
		).execute()
	}
}
