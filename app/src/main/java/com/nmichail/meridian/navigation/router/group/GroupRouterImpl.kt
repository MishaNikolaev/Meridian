package com.nmichail.meridian.navigation.router.group

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import javax.inject.Inject

class GroupRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : GroupRouter {

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

	override fun navigateBack() {
		router.navigateBack()
	}
}
