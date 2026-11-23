package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.JoinedChat
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class AcceptChatInviteUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String) -> JoinedChat by repository::acceptInvite