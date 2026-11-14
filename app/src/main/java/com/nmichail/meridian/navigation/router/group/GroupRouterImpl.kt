package com.nmichail.meridian.navigation.router.group

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.di.ChatSessionHolder
import com.nmichail.meridian.features.conversation.presentation.getConversationScreen
import com.nmichail.meridian.features.groups.presentation.GroupRouter
import com.nmichail.meridian.features.groups.presentation.getGroupEditScreen
import com.nmichail.meridian.features.person.presentation.getPersonScreen
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

	override fun closeChat() {
		router.navigateBack()
		router.navigateBack()
	}

	override fun navigateToEdit(chatId: String, title: String, description: String) {
		if (ChatSessionHolder.current() == null) {
			return
		}
		router.navigateTo(
			getGroupEditScreen(
				chatId = chatId,
				title = title,
				description = description,
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
}
