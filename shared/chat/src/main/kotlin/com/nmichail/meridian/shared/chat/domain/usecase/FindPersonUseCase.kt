package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import javax.inject.Inject

class FindPersonUseCase @Inject constructor(
	private val repository: ChatRepository,
) : suspend (String) -> ChatPerson by repository::findPerson