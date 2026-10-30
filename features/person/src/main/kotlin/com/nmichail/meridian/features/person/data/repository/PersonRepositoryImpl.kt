package com.nmichail.meridian.features.person.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.person.data.api.PersonApi
import com.nmichail.meridian.features.person.data.mapper.toProfile
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import com.nmichail.meridian.features.person.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
	private val api: PersonApi,
	private val sessionRepository: SessionRepository,
) : PersonRepository {

	override suspend fun loadPerson(userId: String): PersonProfile = api.loadPerson(userId).toProfile(
		server = sessionRepository.getHost().orEmpty(),
	)
}