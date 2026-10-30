package com.nmichail.meridian.features.person.data.mapper

import com.nmichail.meridian.features.person.data.model.PublicProfileDto
import com.nmichail.meridian.features.person.domain.entity.PersonProfile

internal fun PublicProfileDto.toProfile(server: String): PersonProfile = PersonProfile(
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