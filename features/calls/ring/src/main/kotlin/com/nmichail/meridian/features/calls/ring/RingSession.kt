package com.nmichail.meridian.features.calls.ring

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase

interface RingSession {

	suspend fun readChat(chatId: String): Chat

	suspend fun updateCall(callId: String, phase: CallPhase)
}
