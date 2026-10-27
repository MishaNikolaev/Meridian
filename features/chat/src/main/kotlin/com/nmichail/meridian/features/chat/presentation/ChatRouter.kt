package com.nmichail.meridian.features.chat.presentation

interface ChatRouter {

	fun navigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	)

	fun navigateToDiscussion(
		chatId: String,
		title: String,
		corporate: Boolean,
		messageId: String,
		personId: String,
	)

	fun navigateToSearch()

	fun navigateToGroup()

	fun navigateToWorkspace()

	fun navigateBack()
}