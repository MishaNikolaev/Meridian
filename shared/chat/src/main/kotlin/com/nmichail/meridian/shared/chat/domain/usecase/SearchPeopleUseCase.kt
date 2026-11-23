package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import javax.inject.Inject

class SearchPeopleUseCase @Inject constructor(
	private val repository: GroupEditRepository,
) : suspend (String) -> List<ChatPerson> by repository::searchPeople