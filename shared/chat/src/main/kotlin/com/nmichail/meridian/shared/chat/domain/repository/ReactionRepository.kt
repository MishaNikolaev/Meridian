package com.nmichail.meridian.shared.chat.domain.repository

import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory

interface ReactionRepository {
	fun readReactions(): ReactionMemory

	fun saveReaction(messageId: String, reactions: Set<String>, saveUsed: Boolean)

	suspend fun react(messageId: String, reaction: String, active: Boolean)
}