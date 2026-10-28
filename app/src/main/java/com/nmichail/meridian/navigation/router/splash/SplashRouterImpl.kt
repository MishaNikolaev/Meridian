package com.nmichail.meridian.navigation.router.splash

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.ui.host.getMainHostScreen
import javax.inject.Inject

class SplashRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : SplashRouter {

	override fun navigateToHome() {
		router.newRootScreen(getMainHostScreen())
	}

	override fun navigateToSignIn() {
		router.newRootScreen(getWorkspaceScreen())
	}
}
