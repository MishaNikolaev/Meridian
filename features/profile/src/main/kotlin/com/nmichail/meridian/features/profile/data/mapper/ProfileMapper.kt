package com.nmichail.meridian.features.profile.data.mapper

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

internal fun ProfileDto.toAccount(email: String): ProfileAccount = ProfileAccount(
	name = displayName,
	email = email,
)
