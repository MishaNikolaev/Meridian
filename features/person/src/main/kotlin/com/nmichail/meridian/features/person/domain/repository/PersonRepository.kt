package com.nmichail.meridian.features.person.domain.repository

import com.nmichail.meridian.features.person.domain.entity.PersonProfile

interface PersonRepository {

	suspend fun loadPerson(userId: String): PersonProfile
}
