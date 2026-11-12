package com.nmichail.meridian.features.person.presentation

sealed interface PersonState {

	data object Initial : PersonState

	data object Loading : PersonState

	data class Content(
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
		val photo: String,
		val corporate: Boolean,
	) : PersonState

	data class Error(
		val message: Int,
	) : PersonState
}