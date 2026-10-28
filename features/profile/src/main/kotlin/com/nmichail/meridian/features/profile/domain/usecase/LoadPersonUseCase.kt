package com.nmichail.meridian.features.profile.domain.usecase

import com.nmichail.meridian.features.profile.domain.entity.PersonCard
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadPersonUseCase @Inject constructor(
	private val repository: ProfileRepository,
) : suspend (String) -> PersonCard by repository::person
