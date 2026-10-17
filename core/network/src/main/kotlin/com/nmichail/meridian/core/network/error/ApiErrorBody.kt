package com.nmichail.meridian.core.network.error

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorBody(
	val code: String,
	val message: String,
)
