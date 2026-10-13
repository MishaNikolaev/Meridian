package com.nmichail.meridian.navigation.router.home

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.home.presentation.HomeRouter
import javax.inject.Inject

class HomeRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : HomeRouter {

	override fun navigateBack() {
		router.navigateBack()
	}
}