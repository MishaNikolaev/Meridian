package com.nmichail.meridian.shared.phone.data.repository

import com.nmichail.meridian.shared.phone.data.api.PhoneApi
import com.nmichail.meridian.shared.phone.data.dto.CallDto
import com.nmichail.meridian.shared.phone.data.dto.CallStateDto
import com.nmichail.meridian.shared.phone.data.dto.CreateCallDto
import com.nmichail.meridian.shared.phone.data.mapper.toCallPhase
import com.nmichail.meridian.shared.phone.data.mapper.toWire
import com.nmichail.meridian.shared.phone.domain.entity.CallMedia
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase
import com.nmichail.meridian.shared.phone.domain.entity.PlacedCall
import com.nmichail.meridian.shared.phone.domain.repository.PhoneRepository
import javax.inject.Inject

class PhoneRepositoryImpl @Inject constructor(
	private val api: PhoneApi,
) : PhoneRepository {

	override suspend fun startCall(chatId: String, media: CallMedia): PlacedCall = api.startCall(
		CreateCallDto(chatId = chatId, media = media.toWire()),
	).placed()

	override suspend fun updateCall(callId: String, phase: CallPhase): PlacedCall = api.updateCall(
		callId = callId,
		body = CallStateDto(state = phase.toWire()),
	).placed()
}

private fun CallDto.placed(): PlacedCall {
	val phase = state.toCallPhase() ?: throw IllegalStateException("Unknown call state: $state")
	return PlacedCall(
		callId = callId,
		chatId = chatId,
		initiatorUserId = initiatorUserId,
		state = phase,
	)
}
