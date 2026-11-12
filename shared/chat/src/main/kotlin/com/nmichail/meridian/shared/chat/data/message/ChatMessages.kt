package com.nmichail.meridian.shared.chat.data.message

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal suspend fun ChatApi.toThread(
	chatId: String,
	self: String?,
	reactions: ReactionDataSource,
	deletions: DeletionDataSource,
): ChatThread = coroutineScope {
	val details = async { getChat(chatId) }

	val page = async { getMessages(chatId) }

	val saved = reactions.getReactions()

	val loaded = page.await()

	deletions.saveDeleted(loaded.items.filter { item -> item.deleted }.map { item -> item.messageId })

	reactions.saveReactions(
		loaded.ownReactions(self).filterKeys { messageId -> messageId !in saved },
	)

	loaded.thread(
		details = details.await(),
		self = self,
		dropped = deletions.getDeleted(),
		saved = reactions.getReactions(),
	)
}