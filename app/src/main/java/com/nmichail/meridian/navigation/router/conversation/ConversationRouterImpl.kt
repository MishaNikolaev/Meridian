package com.nmichail.meridian.navigation.router.conversation

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import com.nmichail.meridian.features.groups.presentation.getGroupInfoScreen
import com.nmichail.meridian.features.person.presentation.getPersonScreen
import javax.inject.Inject

class ConversationRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ConversationRouter {
	override fun navigateToDiscussion(
		chatId: String,
		title: String,
		corporate: Boolean,
		messageId: String,
		personId: String,
	) {
		router.navigateTo(
			getConversationScreen(
				chatId = chatId,
				title = title,
				corporate = corporate,
				messageId = messageId,
				personId = personId,
			),
		)
	}

	override fun navigateToPerson(userId: String, corporate: Boolean) {
		router.navigateTo(
			getPersonScreen(
				userId = userId,
				corporate = corporate,
			),
		)
	}

	override fun navigateToGroup(chatId: String, title: String, members: List<String>, corporate: Boolean) {
		router.navigateTo(
			getGroupInfoScreen(
				chatId = chatId,
				title = title,
				members = members,
				corporate = corporate,
			),
		)
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}