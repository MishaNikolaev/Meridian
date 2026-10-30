package com.nmichail.meridian.features.conversation.presentation

sealed interface ConversationEvent {

	data object NoConnection : ConversationEvent

	data object Error : ConversationEvent
}
