package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.JoinedChat
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class AcceptChatInviteUseCase @Inject constructor(
	private val repository: GroupRepository,
) : suspend (String) -> JoinedChat by repository::acceptInvite