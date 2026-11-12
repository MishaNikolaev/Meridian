package com.nmichail.meridian.features.profile.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class StatusBody(
	val displayName: String?,
	val statusText: String?,
	val position: String?,
	val department: String?,
	val photoId: String?,
)

internal fun StatusBody.toPayload(): JsonObject = buildJsonObject {
	put(key = "displayName", element = displayName.toJson())
	put(key = "statusText", element = statusText.toJson())
	put(key = "position", element = position.toJson())
	put(key = "department", element = department.toJson())
	put(key = "photoId", element = photoId.toJson())
}

private fun String?.toJson(): JsonElement = this?.let(::JsonPrimitive) ?: JsonNull