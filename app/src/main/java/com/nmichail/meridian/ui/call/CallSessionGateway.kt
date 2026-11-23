package com.nmichail.meridian.ui.call

import com.nmichail.meridian.di.ChatSessionHolder
import com.nmichail.meridian.features.calls.ring.RingSession
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatUseCase
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase
import com.nmichail.meridian.shared.phone.domain.usecase.UpdateCallUseCase
import javax.inject.Inject

class CallSessionClosedException : IllegalStateException()

class CallSessionGateway @Inject constructor() : RingSession {

	override suspend fun readChat(chatId: String): Chat = ReadChatUseCase(session().chatRepository())(chatId)

	override suspend fun updateCall(callId: String, phase: CallPhase) {
		UpdateCallUseCase(session().phoneRepository())(callId, phase)
	}

	private fun session() = ChatSessionHolder.current() ?: throw CallSessionClosedException()
}
