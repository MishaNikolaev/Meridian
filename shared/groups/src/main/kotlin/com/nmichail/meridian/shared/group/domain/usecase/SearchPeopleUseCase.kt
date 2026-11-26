package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import javax.inject.Inject

class SearchPeopleUseCase @Inject constructor(
	private val repository: GroupRepository,
) : suspend (String) -> List<ChatPerson> by repository::searchPeople