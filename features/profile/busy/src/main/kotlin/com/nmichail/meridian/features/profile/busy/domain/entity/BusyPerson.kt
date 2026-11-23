package com.nmichail.meridian.features.profile.busy.domain.entity

data class BusyPerson(
	val name: String,
	val slots: Set<Int>,
)