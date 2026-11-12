package com.nmichail.meridian.features.conversation.presentation

interface ConversationRouter {
	fun navigateToDiscussion(
		chatId: String,
		title: String,
		corporate: Boolean,
		messageId: String,
		personId: String,
	)

	fun navigateToPerson(
		userId: String,
		corporate: Boolean,
	)

	fun navigateToGroup(
		chatId: String,
		title: String,
		members: List<String>,
		corporate: Boolean,
	)

	fun navigateBack()
}