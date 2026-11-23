package com.nmichail.meridian.navigation.router.conversation

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.di.ChatSessionHolder
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import com.nmichail.meridian.features.groups.info.presentation.getGroupInfoScreen
import com.nmichail.meridian.features.person.presentation.getPersonScreen
import com.nmichail.meridian.ui.call.getCallScreen
import com.nmichail.meridian.ui.call.getConferenceScreen
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
		if (ChatSessionHolder.current() == null) {
			return
		}
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
		if (ChatSessionHolder.current() == null) {
			return
		}
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

	override fun navigateToConference(code: String) {
		router.navigateTo(getConferenceScreen(code) { router.navigateBack() })
	}
}