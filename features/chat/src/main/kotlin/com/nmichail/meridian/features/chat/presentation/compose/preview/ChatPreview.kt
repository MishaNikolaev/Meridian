package com.nmichail.meridian.features.chat.presentation.compose.preview

import com.nmichail.meridian.features.chat.presentation.ChatSection
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

internal val previewCorporateChats = listOf(
	Chat(
		id = "1",
		type = ChatType.DIRECT,
		title = "Новикова Анна",
		preview = "Макеты уже обновила",
		sentAt = "2026-10-27T03:00:00Z",
		unread = 2,
		corporate = true,
		mentioned = false,
		personId = null,
		discussionRoot = null,
		discussionText = null,
	),
	Chat(
		id = "2",
		type = ChatType.GROUP,
		title = "Мобильная разработка",
		preview = "Созвон в 15:00",
		sentAt = "2026-10-26T03:00:00Z",
		unread = 0,
		corporate = true,
		mentioned = false,
		personId = null,
		discussionRoot = null,
		discussionText = null,
	),
)

internal val previewExternalChats = listOf(
	Chat(
		id = "3",
		type = ChatType.DIRECT,
		title = "Власов Кирилл",
		preview = "Глянь последний фрейм",
		sentAt = "2026-10-27T01:00:00Z",
		unread = 1,
		corporate = false,
		mentioned = false,
		personId = null,
		discussionRoot = null,
		discussionText = null,
	),
)

internal val previewChats = previewCorporateChats + previewExternalChats

internal val previewDiscussions = listOf(
	ChatDiscussion(
		chatId = "2",
		messageId = "root",
		title = "Мобильная разработка",
		text = "Макеты экрана оплаты уже обновил?",
		comment = "Анна: Да, залила",
		sentAt = "2026-10-27T03:00:00Z",
		corporate = true,
		type = ChatType.GROUP,
		personId = null,
	),
)

internal val previewContent = ChatState.Content(
	corporate = true,
	section = ChatSection.ALL,
	corporateChats = previewCorporateChats,
	externalTabChats = previewExternalChats,
	corporateDiscussions = previewDiscussions,
	externalTabDiscussions = emptyList(),
	createOpen = false,
)

internal val previewEmptyContent = ChatState.Content(
	corporate = true,
	section = ChatSection.ALL,
	corporateChats = emptyList(),
	externalTabChats = emptyList(),
	corporateDiscussions = emptyList(),
	externalTabDiscussions = emptyList(),
	createOpen = false,
)

internal val previewDiscussionsContent = ChatState.Content(
	corporate = true,
	section = ChatSection.DISCUSSIONS,
	corporateChats = previewCorporateChats,
	externalTabChats = previewExternalChats,
	corporateDiscussions = previewDiscussions,
	externalTabDiscussions = emptyList(),
	createOpen = false,
)

internal val previewExternalContent = ChatState.Content(
	corporate = false,
	section = ChatSection.ALL,
	corporateChats = previewCorporateChats,
	externalTabChats = previewExternalChats,
	corporateDiscussions = previewDiscussions,
	externalTabDiscussions = emptyList(),
	createOpen = false,
)

internal val previewCreateContent = ChatState.Content(
	corporate = true,
	section = ChatSection.ALL,
	corporateChats = previewCorporateChats,
	externalTabChats = previewExternalChats,
	corporateDiscussions = previewDiscussions,
	externalTabDiscussions = emptyList(),
	createOpen = true,
)
