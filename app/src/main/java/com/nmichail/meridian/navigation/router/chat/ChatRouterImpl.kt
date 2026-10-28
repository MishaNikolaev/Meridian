package com.nmichail.meridian.navigation.router.chat

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.features.groups.presentation.getGroupScreen
import com.nmichail.meridian.features.search.presentation.getSearchScreen
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import javax.inject.Inject

class ChatRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ChatRouter {

	override fun navigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	) {
		router.navigateTo(
			getConversationScreen(
				chatId = chatId,
				title = title,
				corporate = corporate,
				personId = personId,
			),
		)
	}

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

	override fun navigateToSearch() {
		router.navigateTo(getSearchScreen())
	}

	override fun navigateToGroup() {
		router.navigateTo(getGroupScreen())
	}

	override fun navigateToWorkspace() {
		router.newRootScreen(getWorkspaceScreen())
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}
