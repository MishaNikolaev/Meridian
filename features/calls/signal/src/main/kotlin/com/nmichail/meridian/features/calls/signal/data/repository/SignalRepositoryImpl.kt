package com.nmichail.meridian.features.calls.signal.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.calls.signal.data.api.SignalApi
import com.nmichail.meridian.features.calls.signal.data.dto.SignalDeviceDto
import com.nmichail.meridian.features.calls.signal.data.dto.SignalRequestDto
import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SignalRepositoryImpl @Inject constructor(
	private val api: SignalApi,
	private val chats: ChatApi,
	private val members: MemberDataSource,
	private val sessionRepository: SessionRepository,
) : SignalRepository {

	private val gate = Mutex()
	private var chatDevices = emptyMap<String, List<String>>()

	override suspend fun sendSignal(call: SignalCall, body: String) {
		val cipher = MessageCipher.encrypt(call.chatId, body)
		val devices = loadDevices(call.chatId)
		api.send(
			callId = call.callId,
			body = SignalRequestDto(
				devices.map {
					SignalDeviceDto(deviceId = it, body = cipher)
				}
			),
		).execute()
	}

	override suspend fun loadSignals(call: SignalCall): List<String> =
		api.load(call.callId).mapNotNull { message ->
			MessageCipher.decrypt(call.chatId, message.body).takeIf { plain -> plain.isNotEmpty() }
		}

	private suspend fun loadDevices(chatId: String): List<String> {
		chatDevices[chatId]?.let { return it }
		return gate.withLock {
			chatDevices[chatId]?.let { return@withLock it }
			val devices = readDevices(chatId)
			chatDevices = chatDevices + (chatId to devices)
			devices
		}
	}

	private suspend fun readDevices(chatId: String): List<String> {
		val user = sessionRepository.getUserId() ?: throw IllegalStateException()
		val next = mutableListOf<String>()
		for (member in chats.getChat(chatId).members) {
			if (member.userId == user) {
				continue
			}
			for (device in members.getKeys(member.userId).devices) {
				next += device.deviceId
			}
		}
		val distinct = next.distinct()
		if (distinct.isEmpty()) {
			throw IllegalStateException()
		}
		return distinct
	}
}