package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.features.conversation.presentation.compose.component.days
import com.nmichail.meridian.features.conversation.presentation.compose.component.markDeleted
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import java.time.Instant

internal fun helloThread(title: String): ChatThread = ChatThread(
	lines = listOf(
		ChatLine(
			id = "hello",
			mine = false,
			text = "",
			sentAt = Instant.now().toString(),
			author = title,
			authorId = "",
			replyAuthor = null,
			replyText = null,
			threadRoot = null,
			deleted = false,
			edited = false,
			service = false,
			fileId = "",
			fileName = "",
			fileType = "",
			poll = null,
			vote = null,
		),
	),
	members = 0,
	readUpTo = null,
	reactions = emptyMap(),
	reactionViewers = emptyMap(),
	people = emptyList(),
	selfName = "",
)

internal fun ConversationState.Content.withBotMessage(
	text: String,
	replyAuthor: String,
	replyText: String,
): ConversationState.Content {
	val next = lines + ChatLine(
		id = "${lines.size}",
		mine = true,
		text = text,
		sentAt = Instant.now().toString(),
		author = null,
		authorId = "",
		replyAuthor = replyAuthor.ifEmpty { null },
		replyText = replyText.ifEmpty { null },
		threadRoot = rootId,
		deleted = false,
		edited = false,
		service = false,
		fileId = "",
		fileName = "",
		fileType = "",
		poll = null,
		vote = null,
	)

	return copy(
		replyAuthor = "",
		replyName = "",
		replyText = "",
		lines = next,
		days = next.days(),
	)
}

internal fun ConversationState.Content.without(ids: Set<String>): ConversationState.Content {
	val next = lines.map { line ->
		if (line.id in ids) {
			line.markDeleted()
		} else {
			line
		}
	}

	return copy(
		lines = next,
		days = next.days(),
		selectedIds = emptySet(),
		menuLineId = null,
		pinnedId = if (pinnedId in ids) {
			null
		} else {
			pinnedId
		},
		reactions = reactions.filterKeys { it !in ids },
		reactionViewers = reactionViewers.filterKeys { it !in ids },
	)
}
