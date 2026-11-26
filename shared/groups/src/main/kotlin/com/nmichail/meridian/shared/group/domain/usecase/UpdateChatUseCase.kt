package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class UpdateChatUseCase @Inject constructor(
	private val repository: GroupRepository,
) {
	suspend operator fun invoke(
		chatId: String,
		title: String,
		description: String,
		photoId: String,
	): ChatProfile = repository.updateChat(chatId, title, description, photoId)
}