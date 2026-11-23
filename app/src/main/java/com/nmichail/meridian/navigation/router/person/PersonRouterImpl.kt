package com.nmichail.meridian.navigation.router.person

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.di.ChatSessionHolder
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import com.nmichail.meridian.features.person.presentation.PersonRouter
import com.nmichail.meridian.features.person.presentation.getPersonScreen
import com.nmichail.meridian.ui.call.getCallScreen
import javax.inject.Inject

class PersonRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : PersonRouter {

	override fun navigateToPerson(userId: String, corporate: Boolean) {
		router.navigateTo(
			getPersonScreen(
				userId = userId,
				corporate = corporate,
			),
		)
	}

	override fun navigateToConversation(
		chatId: String,
		title: String,
		corporate: Boolean,
		personId: String,
	) {
		if (ChatSessionHolder.current() == null) {
			return
		}

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

	override fun navigateToCall(chatId: String, title: String, personId: String, video: Boolean) {
		router.navigateTo(
			getCallScreen(
				chatId = chatId,
				title = title,
				personId = personId,
				incoming = false,
				callId = "",
				video = video,
				onBack = { router.navigateBack() },
			),
		)
	}
}
