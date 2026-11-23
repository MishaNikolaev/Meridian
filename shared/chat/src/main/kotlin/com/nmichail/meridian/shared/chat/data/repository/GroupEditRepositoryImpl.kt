package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.api.ChatEditApi
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.dto.AddMemberDto
import com.nmichail.meridian.shared.chat.data.dto.PatchChatDto
import com.nmichail.meridian.shared.chat.data.dto.TransferChatDto
import com.nmichail.meridian.shared.chat.data.mapper.displayName
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.data.mapper.toProfile
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.entity.JoinedChat
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class GroupEditRepositoryImpl @Inject constructor(
	private val api: ChatApi,
	private val editApi: ChatEditApi,
	private val chatDataSource: ChatDataSource,
	private val sessionRepository: SessionRepository,
) : GroupEditRepository {
	override suspend fun readChat(chatId: String): ChatProfile {
		val details = api.getChat(chatId)

		val selfId = sessionRepository.getUserId()

		val profile = details.chat.toProfile(
			owner = details.members.displayName(selfId),
			members = details.members.map { member -> member.toEntity() },
			selfId = selfId.orEmpty(),
		)
		if (profile.photoId.isNotEmpty()) {
			PhotoLocalDataSource.keepChatPhoto(chatId = chatId, photoId = profile.photoId)
		}
		return profile
	}

	override suspend fun updateChat(chatId: String, title: String, description: String, photoId: String): ChatProfile {
		val profile = editApi.patchChat(
			chatId = chatId,
			body = PatchChatDto(
				title = title,
				description = description,
				photoId = photoId.takeIf { it.isNotEmpty() },
			),
		).chat.toProfile(
			owner = "",
			members = emptyList(),
			selfId = "",
		)

		if (profile.photoId.isNotEmpty()) {
			PhotoLocalDataSource.keepChatPhoto(chatId = chatId, photoId = profile.photoId)
		} else if (photoId.isNotEmpty()) {
			PhotoLocalDataSource.keepChatPhoto(chatId = chatId, photoId = photoId)
		}

		val stored = chatDataSource.read()

		if (stored != null) {
			chatDataSource.save(
				stored.map { chat ->
					if (chat.id == chatId) {
						chat.copy(title = profile.title)
					} else {
						chat
					}
				},
			)
		}

		return profile
	}

	override suspend fun addChat(chatId: String, userId: String) {
		editApi.addMember(
			chatId = chatId,
			body = AddMemberDto(userId = userId),
		)
	}

	override suspend fun removeChat(chatId: String, userId: String) {
		editApi.removeMember(chatId = chatId, userId = userId)
	}

	override suspend fun transferChat(chatId: String, userId: String) {
		editApi.transfer(
			chatId = chatId,
			body = TransferChatDto(userId = userId),
		)
	}

	override suspend fun promoteChat(chatId: String, userId: String) {
		editApi.promote(
			chatId = chatId,
			body = TransferChatDto(userId = userId),
		)
	}

	override suspend fun leaveChat(chatId: String) {
		editApi.leave(chatId = chatId)

		val stored = chatDataSource.read() ?: return

		chatDataSource.save(stored.filter { chat -> chat.id != chatId })
	}

	override suspend fun searchPeople(query: String): List<ChatPerson> =
		api.getUsers(query).map { person -> person.toEntity() }

	override suspend fun createInvite(chatId: String): String = editApi.invite(chatId).code

	override suspend fun acceptInvite(code: String): JoinedChat {
		val chat = editApi.accept(code).chat
		val photo = chat.photoId.orEmpty()
		if (photo.isNotEmpty()) {
			PhotoLocalDataSource.keepChatPhoto(chatId = chat.chatId, photoId = photo)
		}

		return JoinedChat(
			id = chat.chatId,
			title = chat.title.orEmpty(),
		)
	}
}
