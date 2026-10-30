package com.nmichail.meridian.features.profile.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.profile.data.api.ProfileApi
import com.nmichail.meridian.features.profile.data.mapper.toAccount
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
	private val api: ProfileApi,
	private val sessionRepository: SessionRepository,
) : ProfileRepository {

	override suspend fun loadAccount(): ProfileAccount = api.profile().toAccount(
		email = sessionRepository.email().orEmpty(),
	)
}
