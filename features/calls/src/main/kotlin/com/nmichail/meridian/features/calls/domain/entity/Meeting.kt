package com.nmichail.meridian.features.calls.domain.entity

data class Meeting(
	val name: String,
	val details: String,
	val initials: String,
	val canJoin: Boolean,
)