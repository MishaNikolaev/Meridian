package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class CreateChatInviteUseCase @Inject constructor(
	private val repository: GroupRepository,
) : suspend (String) -> String by repository::createInvite