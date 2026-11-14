@file:Suppress("TooManyFunctions")

package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.dto.CreateChatDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import com.nmichail.meridian.shared.chat.data.dto.ReadKindDto
import com.nmichail.meridian.shared.chat.data.dto.ReadRequestDto
import com.nmichail.meridian.shared.chat.data.mapper.toDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.data.chat.toChats
import com.nmichail.meridian.shared.chat.data.chat.toCreatedChat
import com.nmichail.meridian.shared.chat.data.message.postMessage
import com.nmichail.meridian.shared.chat.data.message.toThread
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val sessionRepository: SessionRepository,
	private val memberDataSource: MemberDataSource,
	private val reactionDataSource: ReactionDataSource,
	private val deletionDataSource: DeletionDataSource,
	private val chatDataSource: ChatDataSource,
) : ChatRepository {
	override suspend fun loadChats(): List<Chat> {
		val chats = api.getChats().toChats(
			api = api,
			self = sessionRepository.getUserId(),
		)

		chatDataSource.save(chats)

		return chats
	}

	override suspend fun create(type: ChatType, title: String, usernames: List<String>): CreatedChat {
		val memberIds = usernames.mapNotNull { name ->
			if (name.isBlank()) {
				null
			} else {
				member(name).userId
			}
		}

		return api.createChat(
			CreateChatDto(
				type = type.toDto(),
				title = title.ifBlank { null },
				memberIds = memberIds,
			),
		).toCreatedChat(self = sessionRepository.getUserId())
	}

	override suspend fun findPerson(username: String): ChatPerson = member(username).toEntity()

	private suspend fun member(username: String): UserCardDto {
		val login = username.trim()

		if (login.isEmpty()) {
			throw GatewayException(code = GatewayCode.USER_NOT_FOUND)
		}

		return memberDataSource.getUsers(login)
			.firstOrNull { user -> user.username.equals(login, ignoreCase = true) }
			?: throw GatewayException(code = GatewayCode.USER_NOT_FOUND)
	}

	override suspend fun loadMessages(chatId: String): ChatThread =
		api.toThread(
			chatId = chatId,
			self = sessionRepository.getUserId(),
			reactions = reactionDataSource,
			deletions = deletionDataSource,
		)

	override fun readReactions(): ReactionMemory = ReactionMemory(
		chosen = reactionDataSource.getReactions(),
		used = reactionDataSource.used(),
	)

	override fun saveReaction(messageId: String, reactions: Set<String>, saveUsed: Boolean) {
		val previous = reactionDataSource.getReactions()[messageId].orEmpty()

		reactionDataSource.saveReactions(messageId, reactions)

		if (saveUsed) {
			(reactions - previous).forEach(reactionDataSource::saveUsed)
		}
	}

	override suspend fun react(messageId: String, reaction: String, active: Boolean) {
		api.setReaction(
			messageId,
			ReactionRequestDto(
				reaction = reaction,
				active = active,
			),
		).execute()
	}

	override suspend fun delete(messageId: String) {
		api.deleteMessages(messageId).execute()

		deletionDataSource.saveDeleted(messageId)
	}

	override suspend fun markRead(messageId: String) {
		api.markRead(
			messageId,
			ReadRequestDto(kind = ReadKindDto.READ),
		).execute()
	}

	override suspend fun send(
		chatId: String,
		text: String,
		replyAuthor: String,
		replyText: String,
		threadRoot: String,
	) {
		api.sendMessages(
			chatId,
			postMessage(
				api = api,
				members = memberDataSource,
				chatId = chatId,
				text = text,
				replyAuthor = replyAuthor,
				replyText = replyText,
				threadRoot = threadRoot,
				self = sessionRepository.getUserId(),
				deviceId = sessionRepository.getDeviceId(),
			),
		)
	}
}