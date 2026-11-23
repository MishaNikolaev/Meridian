package com.nmichail.meridian.features.person.data.mapper

import com.nmichail.meridian.features.person.data.dto.PersonDto
import com.nmichail.meridian.features.person.data.dto.PublicProfileDto
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

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
	photo = photoId.orEmpty(),
)

internal fun Json.readPeople(stored: String): Map<String, PersonDto> = try {
	decodeFromString(stored)
} catch (_: SerializationException) {
	emptyMap()
}

internal fun Json.writePeople(people: Map<String, PersonDto>): String = encodeToString(people)

internal fun PersonProfile.toDto(): PersonDto = PersonDto(
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

internal fun PersonDto.toProfile(): PersonProfile = PersonProfile(
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