package com.nmichail.meridian.features.calls.signal.data.repository

import com.nmichail.meridian.features.calls.signal.data.api.SignalApi
import com.nmichail.meridian.features.calls.signal.data.dto.SignalDeviceDto
import com.nmichail.meridian.features.calls.signal.data.dto.SignalRequestDto
import com.nmichail.meridian.features.calls.signal.domain.repository.CallTargets
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalSealer
import javax.inject.Inject

class SignalRepositoryImpl @Inject constructor(
	private val api: SignalApi,
	private val targets: CallTargets,
	private val sealer: SignalSealer,
) : SignalRepository {

	override suspend fun send(callId: String, chatId: String, body: String) {
		val cipher = sealer.encrypt(chatId, body)
		val devices = targets.deviceIds(chatId)
		api.send(
			callId = callId,
			body = SignalRequestDto(devices.map { SignalDeviceDto(deviceId = it, body = cipher) }),
		).execute()
	}

	override suspend fun load(callId: String, chatId: String): List<String> =
		api.load(callId).mapNotNull { envelope ->
			sealer.decrypt(chatId, envelope.body).takeIf { plain -> plain.isNotEmpty() }
		}
}
