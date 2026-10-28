package com.nmichail.meridian.features.profile.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.profile.data.api.ProfileApi
import com.nmichail.meridian.features.profile.data.mapper.toAccount
import com.nmichail.meridian.features.profile.data.mapper.toCard
import com.nmichail.meridian.features.profile.domain.entity.PersonCard
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
	private val api: ProfileApi,
	private val sessionRepository: SessionRepository,
) : ProfileRepository {

	override suspend fun account(): ProfileAccount = api.profile().toAccount(
		email = sessionRepository.email().orEmpty(),
	)

	override suspend fun person(userId: String): PersonCard = api.person(userId).toCard(
		server = sessionRepository.getHost().orEmpty(),
	)
}
