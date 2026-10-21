package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionStore
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.model.CipherEnvelopeDto
import com.nmichail.meridian.shared.chat.data.model.CreateChatDto
import com.nmichail.meridian.shared.chat.data.model.PostMessageDto
import com.nmichail.meridian.shared.chat.data.model.ReceiptDto
import com.nmichail.meridian.shared.chat.data.model.ReceiptKindDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val sessionStore: SessionStore,
) : ChatRepository {

	override suspend fun loadChats(): List<ChatSummary> {
		val self = sessionStore.getUserId()
		return api.getChats().items.map { item -> item.toSummary(api, self) }
	}

	override suspend fun create(type: ChatType, title: String, username: String): CreatedChat {
		val member = memberId(username)
		val details = api.create(
			CreateChatDto(
				type = type.toDto(),
				title = title.ifBlank { null },
				memberIds = listOfNotNull(member),
			),
		)
		val members = details.members.map { it.userId to it.displayName }
		val name = details.chat.title ?: peerName(sessionStore.getUserId(), members, details.chat.peerUserId)
		return CreatedChat(id = details.chat.chatId, title = name)
	}

	override suspend fun loadMessages(chatId: String): ChatThread {
		val self = sessionStore.getUserId()
		val details = api.getChat(chatId)
		val names = details.members.associate { member -> member.userId to member.displayName }
		val page = api.getMessages(chatId)
		val lines = page.items.map { item -> item.toLine(self, names[item.senderUserId]) }
		val readUpTo = page.items.lastOrNull { item -> !item.deleted }?.messageId
		return ChatThread(lines = lines, members = details.members.size, readUpTo = readUpTo)
	}

	override suspend fun markRead(messageId: String) {
		api.sendReceipt(messageId, ReceiptDto(kind = ReceiptKindDto.READ)).execute { response ->
			response
		}
	}

	override suspend fun send(chatId: String, text: String) {
		val body = MessageCipher.seal(chatId, text)
		val envelopes = targets(chatId).map { deviceId -> CipherEnvelopeDto(recipientDeviceId = deviceId, body = body) }
		api.send(
			chatId,
			PostMessageDto(clientMessageId = UUID.randomUUID().toString(), envelopes = envelopes),
		)
	}

	private suspend fun memberId(username: String): String? {
		val needle = username.trim()
		if (needle.isEmpty()) {
			return null
		}
		val found = api.getUsers(needle).firstOrNull { it.username.equals(needle, ignoreCase = true) }
		return found?.userId ?: throw GatewayException("USER_NOT_FOUND", "Пользователь не зарегистрирован")
	}

	private suspend fun targets(chatId: String): List<String> {
		val self = sessionStore.getUserId()
		val mine = sessionStore.getDeviceId() ?: throw GatewayException("DEVICE_NOT_FOUND", "Устройство не зарегистрировано")
		val details = api.getChat(chatId)
		val others = details.members.filter { it.userId != self }.flatMap { member ->
			api.getBundle(member.userId).devices.map { it.deviceId }
		}
		return (others + mine).distinct()
	}
}
