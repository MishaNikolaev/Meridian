package com.nmichail.meridian.features.chat.presentation

interface ChatRouter {

	fun openConversation(chatId: String, title: String)

	fun navigateBack()
}
