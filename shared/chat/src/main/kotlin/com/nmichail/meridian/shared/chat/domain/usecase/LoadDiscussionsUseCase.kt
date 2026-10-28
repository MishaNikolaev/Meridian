package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import javax.inject.Inject

class LoadDiscussionsUseCase @Inject constructor(
	private val loadMessagesUseCase: LoadMessagesUseCase,
) {

	suspend operator fun invoke(chats: List<Chat>): List<ChatDiscussion> =
		chats
			.flatMap { chat -> chat.rows() }
			.newestFirst()

	suspend fun replaceChat(
		chatId: String,
		chats: List<Chat>,
		discussions: List<ChatDiscussion>,
	): List<ChatDiscussion> {
		val chat = chats.firstOrNull { item -> item.id == chatId }
		val loaded = if (chat == null) {
			emptyList()
		} else {
			chat.rows()
		}
		return discussions
			.filter { discussion -> discussion.chatId != chatId }
			.plus(loaded)
			.newestFirst()
	}

	private fun List<ChatDiscussion>.newestFirst(): List<ChatDiscussion> =
		sortedByDescending { discussion -> discussion.sentAt.orEmpty() }

	private suspend fun Chat.rows(): List<ChatDiscussion> {
		val lines = loadMessagesUseCase(id).lines
		val replies = lines.mapNotNull { line ->
			line.threadRoot?.let { root -> root to line }
		}.groupBy(
			keySelector = { pair -> pair.first },
			valueTransform = { pair -> pair.second },
		)
		return replies.map { (rootId, comments) ->
			toDiscussion(
				rootId = rootId,
				root = lines.firstOrNull { line -> line.id == rootId },
				latest = comments.maxBy { line -> line.sentAt.orEmpty() },
			)
		}
	}

	private fun Chat.toDiscussion(
		rootId: String,
		root: ChatLine?,
		latest: ChatLine,
	): ChatDiscussion {
		val author = latest.author.orEmpty()
		val comment = if (root == null) {
			""
		} else if (author.isBlank()) {
			latest.text
		} else {
			"$author: ${latest.text}"
		}
		return ChatDiscussion(
			chatId = id,
			messageId = rootId,
			title = title,
			text = root?.text ?: latest.text,
			comment = comment,
			sentAt = latest.sentAt ?: root?.sentAt,
			corporate = corporate,
			type = type,
			personId = personId,
		)
	}
}
