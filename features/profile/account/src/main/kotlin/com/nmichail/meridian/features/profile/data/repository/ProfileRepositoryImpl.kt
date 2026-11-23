package com.nmichail.meridian.features.profile.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.profile.data.api.ProfileApi
import com.nmichail.meridian.features.profile.data.mapper.statusPayload
import com.nmichail.meridian.features.profile.data.mapper.toAccount
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
	private val api: ProfileApi,
	private val sessionRepository: SessionRepository,
) : ProfileRepository {

	override suspend fun loadProfile(): ProfileAccount = api.loadProfile().toAccount(
		email = sessionRepository.email().orEmpty(),
		host = sessionRepository.getHost().orEmpty(),
		userId = sessionRepository.getUserId().orEmpty(),
	)

	override suspend fun saveStatus(status: String): String =
		api.saveStatus(status.statusPayload()).statusText.orEmpty()
}
