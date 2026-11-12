package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class ReadChatProfileUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String) -> ChatProfile by repository::readChat