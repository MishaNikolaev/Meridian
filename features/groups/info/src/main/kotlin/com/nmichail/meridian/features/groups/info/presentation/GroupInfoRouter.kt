package com.nmichail.meridian.features.groups.info.presentation

interface GroupInfoRouter {

	fun navigateBack()

	fun exitChat()

	fun navigateToEdit(
		chatId: String,
		title: String,
		description: String,
	)

	fun navigateToPerson(
		userId: String,
		corporate: Boolean,
	)

	fun navigateToCall(
		chatId: String,
		title: String,
		video: Boolean,
	)
}
