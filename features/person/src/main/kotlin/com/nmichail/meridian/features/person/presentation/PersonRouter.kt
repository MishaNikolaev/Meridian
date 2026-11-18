package com.nmichail.meridian.features.person.presentation

interface PersonRouter {

	fun navigateToPerson(userId: String, corporate: Boolean)

	fun navigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	)

	fun navigateBack()
}