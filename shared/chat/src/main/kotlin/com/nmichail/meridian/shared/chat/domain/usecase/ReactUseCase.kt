package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import javax.inject.Inject

class ReactUseCase @Inject constructor(
	private val repository: ReactionRepository,
) : suspend (String, String, Boolean) -> Unit by repository::react