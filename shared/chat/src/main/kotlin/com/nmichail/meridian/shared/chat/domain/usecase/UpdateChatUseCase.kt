package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class UpdateChatUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String, String, String) -> ChatProfile by repository::updateChat