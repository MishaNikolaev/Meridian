package com.nmichail.meridian.core.navigation.router

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

class FullScreenRouter(private val router: Router) {

	private val screens = mutableListOf<Screen>()
	val backStack: List<Screen>
		get() = screens.toList()

	fun navigateTo(screen: Screen) {
		router.navigateTo(screen)
		screens.add(screen)
	}

	fun newRootScreen(screen: Screen) {
		router.newRootScreen(screen)
		screens.clear()
		screens.add(screen)
	}

	fun navigateBack() {
		router.exit()
		screens.removeLastOrNull()
	}

	fun finishChain() {
		router.finishChain()
		screens.clear()
	}

	fun replace(screen: Screen) {
		router.replaceScreen(screen)
		screens.removeLastOrNull()
		screens.add(screen)
	}
}
