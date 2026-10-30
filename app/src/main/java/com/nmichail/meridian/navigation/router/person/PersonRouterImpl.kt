package com.nmichail.meridian.navigation.router.person

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.person.presentation.PersonRouter
import com.nmichail.meridian.features.person.presentation.getPersonScreen
import javax.inject.Inject

class PersonRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : PersonRouter {

	override fun navigateToPerson(userId: String, corporate: Boolean) {
		router.navigateTo(
			getPersonScreen(
				userId = userId,
				corporate = corporate,
			),
		)
	}

	override fun navigateBack() {
		router.navigateBack()
	}
}
