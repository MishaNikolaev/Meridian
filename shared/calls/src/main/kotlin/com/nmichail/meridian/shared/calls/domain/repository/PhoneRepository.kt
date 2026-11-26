package com.nmichail.meridian.shared.calls.domain.repository

import com.nmichail.meridian.shared.calls.domain.entity.CallMedia
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase
import com.nmichail.meridian.shared.calls.domain.entity.PlacedCall

interface PhoneRepository {
	suspend fun startCall(chatId: String, media: CallMedia): PlacedCall

	suspend fun updateCall(callId: String, phase: CallPhase): PlacedCall
}