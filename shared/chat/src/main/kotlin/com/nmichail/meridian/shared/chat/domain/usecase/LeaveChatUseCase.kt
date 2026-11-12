package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class LeaveChatUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String) -> Unit by repository::leaveChat