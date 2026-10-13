package com.nmichail.meridian.navigation.router.main

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.home.presentation.getHomeScreen
import javax.inject.Inject

class MainRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : MainRouter {

	override fun navigateToHome() {
		router.newRootScreen(getHomeScreen())
	}
}
