package com.nmichail.meridian.shared.busy.domain.entity

data class BusyPerson(
	val name: String,
	val slots: Set<Int>,
)
