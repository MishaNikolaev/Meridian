package com.nmichail.meridian.navigation.router.conversation

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import javax.inject.Inject

class ConversationRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : ConversationRouter {

	override fun navigateBack() {
		router.navigateBack()
	}
}
