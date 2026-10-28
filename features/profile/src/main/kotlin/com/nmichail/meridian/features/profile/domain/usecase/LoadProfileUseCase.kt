package com.nmichail.meridian.features.profile.domain.usecase

import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadProfileUseCase @Inject constructor(
	private val repository: ProfileRepository,
) : suspend () -> ProfileAccount by repository::account
