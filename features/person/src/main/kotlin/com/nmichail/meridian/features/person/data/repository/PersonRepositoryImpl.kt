package com.nmichail.meridian.features.person.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.person.data.api.PersonApi
import com.nmichail.meridian.features.person.data.datasource.PersonLocalDataSource
import com.nmichail.meridian.features.person.data.mapper.toEntry
import com.nmichail.meridian.features.person.data.mapper.toProfile
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import com.nmichail.meridian.features.person.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
	private val api: PersonApi,
	private val sessionRepository: SessionRepository,
	private val personLocalDataSource: PersonLocalDataSource,
) : PersonRepository {

	override suspend fun readPerson(userId: String): PersonProfile? =
		personLocalDataSource.readPerson(userId)?.toProfile()

	override suspend fun loadPerson(userId: String): PersonProfile {
		val profile = api.loadPerson(userId).toProfile(
			server = sessionRepository.getHost().orEmpty(),
		)

		personLocalDataSource.savePerson(
			userId = userId,
			entry = profile.toEntry(),
		)

		return profile
	}
}