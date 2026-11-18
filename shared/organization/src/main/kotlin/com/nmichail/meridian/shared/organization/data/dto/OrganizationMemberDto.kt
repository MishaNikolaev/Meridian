package com.nmichail.meridian.shared.organization.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationMemberDto(
	val userId: String,
	val username: String,
	val displayName: String,
	val kind: String,
)