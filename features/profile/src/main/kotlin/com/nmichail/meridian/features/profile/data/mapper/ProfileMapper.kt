package com.nmichail.meridian.features.profile.data.mapper

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

internal fun ProfileDto.toAccount(email: String, host: String, userId: String): ProfileAccount = ProfileAccount(
	name = displayName,
	email = email,
	status = statusText.orEmpty(),
	position = position.orEmpty(),
	department = department.orEmpty(),
	host = host,
	photo = photoId.orEmpty(),
	userId = userId,
)
