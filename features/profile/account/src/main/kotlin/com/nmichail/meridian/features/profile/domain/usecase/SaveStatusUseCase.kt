package com.nmichail.meridian.features.profile.domain.usecase

import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SaveStatusUseCase @Inject constructor(
	private val repository: ProfileRepository,
) : suspend (String) -> String by repository::saveStatus