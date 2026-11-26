package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class ReadChatProfileUseCase @Inject constructor(
	private val repository: GroupRepository,
) : suspend (String) -> ChatProfile by repository::readChat