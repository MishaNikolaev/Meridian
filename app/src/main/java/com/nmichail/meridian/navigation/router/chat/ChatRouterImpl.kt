package com.nmichail.meridian.navigation.router.chat

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.features.conversation.presentation.ConversationDestination
import javax.inject.Inject

class ChatRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ChatRouter {

	override fun openConversation(chatId: String, title: String) {
		router.navigateTo(ConversationDestination(chatId, title))
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}
