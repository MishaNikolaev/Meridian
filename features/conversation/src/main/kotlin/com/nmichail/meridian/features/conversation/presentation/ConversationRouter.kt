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

	fun navigateToCall(chatId: String, title: String, personId: String, video: Boolean)

	fun navigateToConference(code: String)
}

internal fun conferenceCode(url: String): String? {
	val link = "meridian.ftc.ru/c/"
	val start = url.indexOf(link, ignoreCase = true)
	if (start < 0) {
		return null
	}
	val code = url.substring(start + link.length).substringBefore('/').substringBefore('?')
	return code.takeIf { it.isNotEmpty() }
}