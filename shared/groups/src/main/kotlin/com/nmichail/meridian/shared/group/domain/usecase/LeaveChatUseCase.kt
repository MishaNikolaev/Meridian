package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class LeaveChatUseCase @Inject constructor(
	private val repository: GroupRepository,
) : suspend (String) -> Unit by repository::leaveChat