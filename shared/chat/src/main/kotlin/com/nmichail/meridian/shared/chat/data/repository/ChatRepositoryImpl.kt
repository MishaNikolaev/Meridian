@file:Suppress("TooManyFunctions")

package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.cipher.encodeOpenedMessage
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.ChatPageDto
import com.nmichail.meridian.shared.chat.data.dto.CreateChatDto
import com.nmichail.meridian.shared.chat.data.dto.DeviceMessageDto
import com.nmichail.meridian.shared.chat.data.dto.MessagePageDto
import com.nmichail.meridian.shared.chat.data.dto.PostMessageDto
import com.nmichail.meridian.shared.chat.data.dto.ReadKindDto
import com.nmichail.meridian.shared.chat.data.dto.ReadRequestDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import com.nmichail.meridian.shared.chat.data.mapper.toDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.entity.markDeleted
import com.nmichail.meridian.shared.chat.domain.entity.toText
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ChatRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val sessionRepository: SessionRepository,
	private val memberDataSource: MemberDataSource,
	private val reactionDataSource: ReactionDataSource,
	private val messageDataSource: MessageDataSource,
	private val chatDataSource: ChatDataSource,
) : ChatRepository {
	override suspend fun readChat(chatId: String): Chat {
		val dto = api.getChat(chatId).chat
		val title = dto.title?.takeIf { value -> value.isNotBlank() } ?: dto.personName.orEmpty()

		return dto.toEntity(mentioned = false).copy(title = title)
	}

	override suspend fun loadChats(): List<Chat> = api.getChats().toChats()

	override suspend fun saveChats(chats: List<Chat>) {
		chatDataSource.saveChats(chats)
	}

	override suspend fun selfUsername(chatId: String): String {
		val self = sessionRepository.getUserId() ?: return ""

		return api.getChat(chatId).members
			.firstOrNull { member -> member.userId == self }
			?.username
			.orEmpty()
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
		).createdChat(self = sessionRepository.getUserId())
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

	override suspend fun loadMessages(chatId: String): ChatThread = loadThread(chatId)

	override suspend fun delete(messageId: String) {
		api.deleteMessages(messageId).execute()

		messageDataSource.saveDeleted(messageId)
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
			post(
				chatId = chatId,
				text = text,
				replyAuthor = replyAuthor,
				replyText = replyText,
				threadRoot = threadRoot,
				fileId = fileId,
				fileName = fileName,
				fileType = fileType,
				photoId = photoId,
			),
		)
	}

	private fun ChatPageDto.toChats(): List<Chat> =
		items.map { item ->
			val photo = item.photoId.orEmpty()

			if (photo.isNotEmpty()) {
				PhotoLocalDataSource.keepChatPhoto(chatId = item.chatId, photoId = photo)
			}

			item.toEntity(mentioned = false).copy(
				title = item.title ?: item.personName ?: "Чат",
			)
		}

	private fun ChatDetailsDto.createdChat(self: String?): CreatedChat =
		CreatedChat(
			id = chat.chatId,
			title = chat.title ?: personName(self, members, chat.personId),
			corporate = chat.corporate,
		)

	private fun personName(self: String?, members: List<ChatMemberDto>, personId: String?): String {
		val person = members.firstOrNull { member -> member.userId == personId }
			?: members.firstOrNull { member -> member.userId != self }

		return person?.displayName ?: "Чат"
	}

	private suspend fun loadThread(chatId: String): ChatThread = coroutineScope {
		val self = sessionRepository.getUserId()
		val details = async { api.getChat(chatId) }
		val page = async { api.getMessages(chatId) }
		val saved = reactionDataSource.loadReactions()
		val loaded = page.await()

		messageDataSource.saveDeleted(
			loaded.items.filter { item -> item.deleted }.map { item -> item.messageId },
		)
		reactionDataSource.saveReactions(
			loaded.ownReactions(self).filterKeys { messageId -> messageId !in saved },
		)

		loaded.thread(
			details = details.await(),
			self = self,
			dropped = messageDataSource.loadDeleted(),
			saved = reactionDataSource.loadReactions(),
		)
	}

	private fun MessagePageDto.ownReactions(self: String?): Map<String, Set<String>> =
		items.mapNotNull { item ->
			val reactions = item.reactions
				.filter { reaction -> reaction.userId == self }
				.map { reaction -> reaction.reaction }
				.toSet()

			if (reactions.isEmpty()) {
				null
			} else {
				item.messageId to reactions
			}
		}.toMap()

	private fun MessagePageDto.thread(
		details: ChatDetailsDto,
		self: String?,
		dropped: Set<String>,
		saved: Map<String, Set<String>>,
	): ChatThread {
		val names = details.members.associate { member -> member.userId to member.displayName }
		val lines = items.map { item ->
			val line = item.toEntity(
				self = self,
				author = names[item.senderUserId],
			)

			if (item.messageId in dropped) {
				line.markDeleted()
			} else {
				line
			}
		}

		return ChatThread(
			lines = lines,
			members = details.members.size,
			readUpTo = items.lastOrNull { item -> !item.deleted }?.messageId,
			reactions = savedReactions(self, saved, lines),
			reactionViewers = reactionViewers(self, names),
			people = details.members
				.filter { member -> member.userId != self }
				.map { member -> member.toEntity() },
			selfName = names[self].orEmpty(),
		)
	}

	private fun MessagePageDto.savedReactions(
		self: String?,
		saved: Map<String, Set<String>>,
		lines: List<ChatLine>,
	): Map<String, Set<String>> {
		val added = ownReactions(self).filterKeys { messageId -> messageId !in saved }

		return (saved + added).filterKeys { messageId -> lines.any { line -> line.id == messageId } }
	}

	private fun MessagePageDto.reactionViewers(
		self: String?,
		names: Map<String, String>,
	): Map<String, List<ChatReaction>> =
		items.associate { item ->
			item.messageId to item.reactions.map { reaction ->
				reaction.toEntity(
					name = names[reaction.userId].orEmpty(),
					mine = reaction.userId == self,
				)
			}
		}.filterValues { reactions -> reactions.isNotEmpty() }

	private suspend fun post(
		chatId: String,
		text: String,
		replyAuthor: String,
		replyText: String,
		threadRoot: String,
		fileId: String,
		fileName: String,
		fileType: String,
		photoId: List<String>,
	): PostMessageDto {
		val body = MessageCipher.encrypt(
			chatId = chatId,
			value = encodeOpenedMessage(
				text = text,
				replyAuthor = replyAuthor,
				replyText = replyText,
				fileId = fileId,
				fileName = fileName,
				fileType = fileType,
			),
		)
		val mine = sessionRepository.getDeviceId() ?: throw GatewayException(code = GatewayCode.DEVICE_NOT_FOUND)
		val self = sessionRepository.getUserId()
		val others = mutableListOf<String>()

		for (member in api.getChat(chatId).members) {
			if (member.userId == self) {
				continue
			}

			for (device in memberDataSource.getKeys(member.userId).devices) {
				others += device.deviceId
			}
		}

		return PostMessageDto(
			clientMessageId = UUID.randomUUID().toString(),
			deviceMessages = (others + mine).distinct().map { target ->
				DeviceMessageDto(
					deviceId = target,
					body = body,
				)
			},
			threadRoot = threadRoot.takeIf { root -> root.isNotEmpty() },
			photoId = photoId,
		)
	}
}