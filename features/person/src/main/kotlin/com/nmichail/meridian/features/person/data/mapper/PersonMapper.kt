package com.nmichail.meridian.features.person.data.mapper

import com.nmichail.meridian.features.person.data.model.PersonEntry
import com.nmichail.meridian.features.person.data.model.PublicProfileDto
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import java.util.Locale

internal fun PublicProfileDto.toProfile(server: String): PersonProfile = PersonProfile(
	name = displayName,
	position = position.orEmpty().capitalized(),
	department = department.orEmpty(),
	office = office.orEmpty(),
	server = server,
	status = statusText.orEmpty(),
	manager = managerName.orEmpty(),
	managerId = managerUserId.orEmpty(),
	email = email.orEmpty(),
	phone = phone.orEmpty(),
	photo = photoId.orEmpty(),
)

internal fun PersonProfile.toEntry(): PersonEntry = PersonEntry(
	name = name,
	position = position,
	department = department,
	office = office,
	server = server,
	status = status,
	manager = manager,
	managerId = managerId,
	email = email,
	phone = phone,
	photo = photo,
)

internal fun PersonEntry.toProfile(): PersonProfile = PersonProfile(
	name = name,
	position = position,
	department = department,
	office = office,
	server = server,
	status = status,
	manager = manager,
	managerId = managerId,
	email = email,
	phone = phone,
	photo = photo,
)

private fun String.capitalized(): String {
	if (isEmpty()) {
		return this
	}

	return replaceFirstChar { char ->
		if (char.isLowerCase()) {
			char.titlecase(Locale.getDefault())
		} else {
			char.toString()
		}
	}
}