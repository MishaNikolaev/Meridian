package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.cipher.sealPlain
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.mapper.ownReactions
import com.nmichail.meridian.shared.chat.data.mapper.toCreated
import com.nmichail.meridian.shared.chat.data.mapper.toDto
import com.nmichail.meridian.shared.chat.data.mapper.toPerson
import com.nmichail.meridian.shared.chat.data.mapper.toSummary
import com.nmichail.meridian.shared.chat.data.mapper.toThread
import com.nmichail.meridian.shared.chat.data.dto.CipherEnvelopeDto
import com.nmichail.meridian.shared.chat.data.dto.CreateChatDto
import com.nmichail.meridian.shared.chat.data.dto.PostMessageDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import com.nmichail.meridian.shared.chat.data.dto.ReceiptDto
import com.nmichail.meridian.shared.chat.data.dto.ReceiptKindDto
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val sessionRepository: SessionRepository,
	private val memberDataSource: MemberDataSource,
	private val reactionDataSource: ReactionDataSource,
	private val deletionDataSource: DeletionDataSource,
) : ChatRepository {

	override suspend fun loadChats(): List<Chat> {
		val self = sessionRepository.getUserId()
		return api.getChats().items.map { item -> item.toSummary(api, self) }
	}

	override suspend fun create(type: ChatType, title: String, usernames: List<String>): CreatedChat {
		val ids = usernames.mapNotNull { name -> memberDataSource.memberId(name) }
		val details = api.create(
			CreateChatDto(
				type = type.toDto(),
				title = title.ifBlank { null },
				memberIds = ids,
			),
		)
		return details.toCreated(sessionRepository.getUserId())
	}

	override suspend fun person(username: String): ChatPerson {
		val card = memberDataSource.memberCard(username) ?: throw GatewayException(code = "USER_NOT_FOUND")
		return card.toPerson()
	}

	override suspend fun loadMessages(chatId: String): ChatThread {
		val self = sessionRepository.getUserId()
		val details = api.getChat(chatId)
		val page = api.getMessages(chatId)
		val known = reactionDataSource.read()
		page.items.filter { item -> item.deleted }.forEach { item -> deletionDataSource.remember(item.messageId) }
		page.ownReactions(self).filterKeys { id -> id !in known }.forEach { (id, token) ->
			reactionDataSource.remember(id, token)
		}
		return page.toThread(
			details = details,
			self = self,
			dropped = deletionDataSource.read(),
			known = reactionDataSource.read(),
		)
	}

	override fun readReactions(): Map<String, String> = reactionDataSource.read()

	override fun rememberReaction(messageId: String, reaction: String) {
		reactionDataSource.remember(messageId, reaction)
	}

	override suspend fun react(messageId: String, reaction: String) {
		reactionDataSource.remember(messageId, reaction)
		api.react(messageId, ReactionRequestDto(reaction = reaction)).execute { response ->
			response
		}
	}

	override suspend fun delete(messageId: String) {
		api.delete(messageId).execute { response ->
			response
		}
		deletionDataSource.remember(messageId)
	}

	override suspend fun markRead(messageId: String) {
		api.sendReceipt(messageId, ReceiptDto(kind = ReceiptKindDto.READ)).execute { response ->
			response
		}
	}

	override suspend fun send(
		chatId: String,
		text: String,
		replyAuthor: String,
		replyText: String,
		threadRoot: String,
	) {
		val body = MessageCipher.seal(chatId, sealPlain(text, replyAuthor, replyText))
		val envelopes = memberDataSource.targets(chatId).map { deviceId ->
			CipherEnvelopeDto(recipientDeviceId = deviceId, body = body)
		}
		api.send(
			chatId,
			PostMessageDto(
				clientMessageId = UUID.randomUUID().toString(),
				envelopes = envelopes,
				threadRoot = threadRoot.takeIf { it.isNotEmpty() },
			),
		)
	}
}
