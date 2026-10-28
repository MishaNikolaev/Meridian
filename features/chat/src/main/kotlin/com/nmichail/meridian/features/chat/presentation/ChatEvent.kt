package com.nmichail.meridian.features.chat.presentation

sealed interface ChatEvent {

	data object NoConnection : ChatEvent

	data object Error : ChatEvent
}