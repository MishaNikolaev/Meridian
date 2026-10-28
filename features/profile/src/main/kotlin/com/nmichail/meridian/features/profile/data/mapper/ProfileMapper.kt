package com.nmichail.meridian.features.profile.data.mapper

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import com.nmichail.meridian.features.profile.data.model.PublicProfileDto
import com.nmichail.meridian.features.profile.domain.entity.PersonCard
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount

internal fun ProfileDto.toAccount(email: String): ProfileAccount = ProfileAccount(
	name = displayName,
	email = email,
)

internal fun PublicProfileDto.toCard(server: String): PersonCard = PersonCard(
	name = displayName,
	position = position.orEmpty(),
	department = department.orEmpty(),
	office = office.orEmpty(),
	server = server,
	status = statusText.orEmpty(),
	manager = managerName.orEmpty(),
	managerId = managerUserId.orEmpty(),
	email = email.orEmpty(),
	phone = phone.orEmpty(),
)
