package com.nmichail.meridian.features.profile.domain.entity

data class ProfileAccount(
	val name: String,
	val email: String,
	val status: String,
	val position: String,
	val department: String,
	val host: String,
	val photo: String,
	val userId: String,
)