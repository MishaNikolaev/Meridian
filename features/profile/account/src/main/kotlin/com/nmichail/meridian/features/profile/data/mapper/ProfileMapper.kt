package com.nmichail.meridian.features.profile.data.mapper

import com.nmichail.meridian.features.profile.data.model.ProfileDto
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

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

internal fun String.statusPayload(): JsonObject = buildJsonObject {
	put(key = "displayName", element = JsonNull)
	put(key = "statusText", element = JsonPrimitive(this@statusPayload))
	put(key = "position", element = JsonNull)
	put(key = "department", element = JsonNull)
	put(key = "photoId", element = JsonNull)
}
