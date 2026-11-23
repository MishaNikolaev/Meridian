package com.nmichail.meridian.features.conversation.presentation

sealed interface ConversationEvent {

	data object NoConnection : ConversationEvent

	data object Error : ConversationEvent

	data object FileRejected : ConversationEvent

	data object CannotCallSelf : ConversationEvent

	data class OpenedFile(
		val name: String,
		val type: String,
		val bytes: ByteArray,
		val fileId: String,
	) : ConversationEvent
}