package com.nmichail.meridian.core.navigation.router

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen

class SectionRouter(private val sectionRouter: Router) {

	var backStackScreenCount: Int = 0
		private set

	fun navigateTo(screen: Screen) {
		sectionRouter.navigateTo(screen)
		backStackScreenCount += 1
	}

	fun replace(fragmentScreen: FragmentScreen) {
		sectionRouter.replaceScreen(fragmentScreen)
	}

	fun newRootScreen(fragmentScreen: FragmentScreen) {
		sectionRouter.newRootScreen(fragmentScreen)
		backStackScreenCount = 1
	}

	fun clear() {
		sectionRouter.backTo(null)
		backStackScreenCount = 0
	}

	fun navigateBack() {
		sectionRouter.exit()
		backStackScreenCount -= 1
	}
}
