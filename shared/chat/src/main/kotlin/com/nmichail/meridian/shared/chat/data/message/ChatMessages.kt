package com.nmichail.meridian.shared.chat.data.message

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread

internal suspend fun ChatApi.toThread(
	chatId: String,
	self: String?,
	reactions: ReactionDataSource,
	deletions: DeletionDataSource,
): ChatThread {
	
	val details = getChat(chatId)
	val page = getMessages(chatId)
	val saved = reactions.getReactions()

	page.items.filter { item -> item.deleted }.forEach { item ->
		deletions.saveDeleted(item.messageId)
	}

	page.ownReactions(self)
		.filterKeys { messageId -> messageId !in saved }
		.forEach { (messageId, token) ->
			reactions.saveReaction(messageId, token)
		}

	return page.thread(
		details = details,
		self = self,
		dropped = deletions.getDeleted(),
		saved = reactions.getReactions(),
	)
}