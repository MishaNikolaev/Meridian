package com.nmichail.meridian.features.person.presentation

interface PersonRouter {

	fun navigateToPerson(userId: String, corporate: Boolean)

	fun navigateBack()
}