package com.nmichail.meridian.features.profile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
	val displayName: String,
)

@Serializable
data class PublicProfileDto(
	val displayName: String,
	val statusText: String? = null,
	val position: String? = null,
	val department: String? = null,
	val office: String? = null,
	val managerName: String? = null,
	val managerUserId: String? = null,
	val email: String? = null,
	val phone: String? = null,
)
