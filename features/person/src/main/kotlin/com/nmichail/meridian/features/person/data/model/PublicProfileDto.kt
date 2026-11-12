package com.nmichail.meridian.features.person.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PublicProfileDto(
	val displayName: String,
	val statusText: String?,
	val position: String?,
	val department: String?,
	val office: String?,
	val managerName: String?,
	val managerUserId: String?,
	val email: String?,
	val phone: String?,
	val photoId: String?,
)