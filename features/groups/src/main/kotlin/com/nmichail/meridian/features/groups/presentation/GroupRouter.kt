package com.nmichail.meridian.features.groups.presentation

interface GroupRouter {

	fun navigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	)

	fun navigateBack()
}
