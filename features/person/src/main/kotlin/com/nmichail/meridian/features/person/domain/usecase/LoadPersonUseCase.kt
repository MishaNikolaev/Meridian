package com.nmichail.meridian.features.person.domain.usecase

import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import com.nmichail.meridian.features.person.domain.repository.PersonRepository
import javax.inject.Inject

class LoadPersonUseCase @Inject constructor(
	private val repository: PersonRepository,
) : suspend (String) -> PersonProfile by repository::loadPerson
