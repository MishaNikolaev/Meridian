package com.nmichail.meridian.navigation.router.main

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.authorization.splash.presentation.getSplashScreen
import com.nmichail.meridian.features.authorization.workspace.presentation.getWorkspaceScreen
import com.nmichail.meridian.ui.host.getMainHostScreen
import javax.inject.Inject

class MainRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : MainRouter {

	override fun navigateToHome() {
		router.newRootScreen(getMainHostScreen())
	}

	override fun navigateToSignIn() {
		router.newRootScreen(getWorkspaceScreen())
	}

	override fun navigateToSplash() {
		router.newRootScreen(getSplashScreen())
	}
}