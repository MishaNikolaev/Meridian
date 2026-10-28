package com.nmichail.meridian.features.profile.domain.repository

import com.nmichail.meridian.features.profile.domain.entity.PersonCard
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

interface ProfileRepository {

	suspend fun account(): ProfileAccount

	suspend fun person(userId: String): PersonCard
}
