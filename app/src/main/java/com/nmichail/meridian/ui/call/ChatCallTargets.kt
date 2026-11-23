package com.nmichail.meridian.ui.call

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.features.calls.signal.domain.repository.CallDevicesUnavailableException
import com.nmichail.meridian.features.calls.signal.domain.repository.CallTargets
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChatCallTargets @Inject constructor(
	private val chats: ChatApi,
	private val members: MemberDataSource,
	private val sessionRepository: SessionRepository,
) : CallTargets {

	private val gate = Mutex()
	private var cachedChat = ""
	private var cachedDevices = emptyList<String>()

	override suspend fun deviceIds(chatId: String): List<String> = gate.withLock {
		if (cachedChat == chatId && cachedDevices.isNotEmpty()) {
			return@withLock cachedDevices
		}
		val self = sessionRepository.getUserId() ?: throw CallDevicesUnavailableException()
		val next = mutableListOf<String>()
		for (member in chats.getChat(chatId).members) {
			if (member.userId == self) {
				continue
			}
			for (device in members.getKeys(member.userId).devices) {
				next += device.deviceId
			}
		}
		val distinct = next.distinct()
		if (distinct.isEmpty()) {
			throw CallDevicesUnavailableException()
		}
		cachedChat = chatId
		cachedDevices = distinct
		distinct
	}
}
