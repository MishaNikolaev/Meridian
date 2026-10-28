package com.nmichail.meridian.shared.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReactionDto(
	val userId: String,
	val reaction: String,
	val createdAt: String?,
)
