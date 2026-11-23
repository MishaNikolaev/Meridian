package com.nmichail.meridian.features.profile.domain.repository

import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

interface ProfileRepository {
	suspend fun loadProfile(): ProfileAccount

	suspend fun saveStatus(status: String): String
}
