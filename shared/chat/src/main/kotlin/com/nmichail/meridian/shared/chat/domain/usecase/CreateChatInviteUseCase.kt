package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class CreateChatInviteUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String) -> String by repository::createInvite