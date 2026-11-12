package com.nmichail.meridian.features.groups.presentation

data class GroupMate(
	val name: String,
	val self: Boolean,
	val admin: Boolean,
	val userId: String,
	val owner: Boolean,
)