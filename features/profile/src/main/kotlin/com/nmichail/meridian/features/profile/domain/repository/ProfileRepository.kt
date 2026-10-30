package com.nmichail.meridian.features.profile.domain.repository

import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

interface ProfileRepository {

	suspend fun loadAccount(): ProfileAccount
}
