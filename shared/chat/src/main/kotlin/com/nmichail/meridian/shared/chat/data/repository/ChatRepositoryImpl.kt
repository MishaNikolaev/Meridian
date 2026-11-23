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
import com.nmichail.meridian.shared.chat.data.dto.ReadKindDto
import com.nmichail.meridian.shared.chat.data.dto.ReadRequestDto
import com.nmichail.meridian.shared.chat.data.mapper.toDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.data.mapper.toText
import com.nmichail.meridian.shared.chat.data.chat.toChats
import com.nmichail.meridian.shared.chat.data.chat.toCreatedChat
import com.nmichail.meridian.shared.chat.data.message.postMessage
import com.nmichail.meridian.shared.chat.data.message.toThread
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
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
	private val chatDataSource: ChatDataSource,
) : ChatRepository {
	override suspend fun readChat(chatId: String): Chat {
		val dto = api.getChat(chatId).chat
		val title = dto.title?.takeIf { value -> value.isNotBlank() } ?: dto.personName.orEmpty()

		return dto.toEntity(mentioned = false).copy(title = title)
	}

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
			val login = name.trim()

			when {
				login.isEmpty() -> null
				login.knownId() -> login
				else -> member(login).userId
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

	private fun String.knownId(): Boolean {
		val parsed = runCatching { UUID.fromString(this) }.getOrNull() ?: return false

		return parsed.toString().equals(this, ignoreCase = true)
	}

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

	override suspend fun sendNote(chatId: String, threadRoot: String, note: ChatNote) {
		val text = when (note) {
			is ChatNote.PollBody -> note.poll.toText()
			is ChatNote.VoteBody -> note.vote.toText()
		}

		sendMessage(
			chatId = chatId,
			text = text,
			replyAuthor = "",
			replyText = "",
			threadRoot = threadRoot,
			fileId = "",
			fileName = "",
			fileType = "",
			photoId = emptyList(),
		)
	}

	override suspend fun sendMessage(
		chatId: String,
		text: String,
		replyAuthor: String,
		replyText: String,
		threadRoot: String,
		fileId: String,
		fileName: String,
		fileType: String,
		photoId: List<String>,
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
				fileId = fileId,
				fileName = fileName,
				fileType = fileType,
				photoId = photoId,
				self = sessionRepository.getUserId(),
				deviceId = sessionRepository.getDeviceId(),
			),
		)
	}
}