package com.nmichail.meridian.features.person.domain.entity

data class PersonProfile(
	val name: String,
	val position: String,
	val department: String,
	val office: String,
	val server: String,
	val status: String,
	val manager: String,
	val managerId: String,
	val email: String,
	val phone: String,
)
