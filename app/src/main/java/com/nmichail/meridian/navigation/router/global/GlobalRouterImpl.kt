package com.nmichail.meridian.navigation.router.global

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.nmichail.meridian.core.navigation.router.FullScreenRouter
import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.MainHostScreen
import com.nmichail.meridian.core.navigation.screen.SectionScreen
import com.nmichail.meridian.core.navigation.section.SectionNames
import kotlinx.coroutines.flow.StateFlow

class GlobalRouterImpl(
	private val fullScreenRouter: FullScreenRouter,
	private val sectionsHostRouter: SectionsHostRouter,
	private val context: Context,
) : GlobalRouter {

	override val currentSection: StateFlow<SectionNames?> = sectionsHostRouter.currentSection

	override fun newRootScreen(fragmentScreen: FragmentScreen) {
		if (fragmentScreen !is FullScreen && fullScreenRouter.backStack.lastOrNull() is MainHostScreen) {
			sectionsHostRouter.newRootScreen(fragmentScreen)
		} else {
			fullScreenRouter.newRootScreen(fragmentScreen)
		}
	}

	override fun navigateTo(screen: Screen) {
		when (screen) {
			is FullScreen -> fullScreenRouter.navigateTo(screen)
			is SectionScreen -> sectionsHostRouter.openSection(screen)
			else -> sectionsHostRouter.openScreenInSection(screen)
		}
	}

	override fun replace(fragmentScreen: FragmentScreen) {
		when {
			fragmentScreen is FullScreen -> fullScreenRouter.replace(fragmentScreen)
			fragmentScreen !is SectionScreen -> sectionsHostRouter.replaceScreenInSection(fragmentScreen)
		}
	}

	override fun navigateBack() {
		if (fullScreenRouter.backStack.lastOrNull() is MainHostScreen) {
			if (sectionsHostRouter.navigateBack() == SectionsHostRouter.HostStatus.EMPTY) {
				fullScreenRouter.finishChain()
			}
		} else {
			fullScreenRouter.navigateBack()
		}
	}

	override fun openLink(url: String) {
		val intent = Intent(Intent.ACTION_VIEW).apply {
			data = Uri.parse(url)
			flags = Intent.FLAG_ACTIVITY_NEW_TASK
		}
		context.startActivity(intent)
	}
}
