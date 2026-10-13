package com.nmichail.meridian.navigation.router.global

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.nmichail.meridian.core.navigation.router.SectionRouter
import com.nmichail.meridian.core.navigation.screen.SectionScreen
import com.nmichail.meridian.core.navigation.section.SectionNames
import com.nmichail.meridian.features.home.presentation.getHomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SectionsHostRouter(
	private val mainHostRouter: Router,
	private val sectionRouterMap: Map<SectionNames, SectionRouter>,
) {

	private val sectionRoots: Map<SectionNames, () -> FragmentScreen> = mapOf(
		SectionNames.HOME to { getHomeScreen() },
	)

	private val sectionBackStack = mutableListOf<SectionScreen>()

	private val currentSectionFlow = MutableStateFlow<SectionNames?>(null)
	val currentSection: StateFlow<SectionNames?> = currentSectionFlow.asStateFlow()

	fun newRootScreen(screen: FragmentScreen) {
		if (screen is SectionScreen) {
			mainHostRouter.newRootScreen(screen)
			sectionBackStack.clear()
			sectionBackStack.add(screen)
		} else {
			newRootScreenInSection(screen)
		}
	}

	fun openScreenInSection(screen: Screen) {
		sectionRouterMap[currentSection.value]?.navigateTo(screen)
	}

	fun replaceScreenInSection(screen: FragmentScreen) {
		sectionRouterMap[currentSection.value]?.replace(screen)
	}

	fun openSection(sectionScreen: SectionScreen) {
		val desiredSectionName = SectionNames.valueOf(sectionScreen.screenKey)
		if (desiredSectionName == currentSection.value) {
			popToRootInSection(desiredSectionName)
		} else {
			val sectionIndex = findSectionIndex(sectionScreen)
			if (sectionIndex != null && sectionIndex > 0) {
				sectionBackStack.removeAt(sectionIndex)
			}
			sectionBackStack.add(sectionScreen)
			currentSectionFlow.value = desiredSectionName
			mainHostRouter.navigateTo(sectionScreen)
		}
	}

	fun navigateBack(): HostStatus {
		val backStackScreenCount = sectionRouterMap[currentSection.value]?.backStackScreenCount
			?: throw IllegalArgumentException("Current SectionRouter is null")
		return when {
			backStackScreenCount > MIN_SCREEN_IN_BACK_STACK -> {
				sectionRouterMap[currentSection.value]?.navigateBack()
				HostStatus.NOT_EMPTY
			}

			sectionBackStack.size > MIN_SCREEN_IN_BACK_STACK -> {
				sectionBackStack.removeAt(sectionBackStack.lastIndex)
				mainHostRouter.replaceScreen(sectionBackStack.last())
				currentSectionFlow.value = SectionNames.valueOf(sectionBackStack.last().screenKey)
				HostStatus.NOT_EMPTY
			}

			else -> {
				clearSections()
				HostStatus.EMPTY
			}
		}
	}

	fun clearSections() {
		sectionRouterMap.values.forEach { it.clear() }
		currentSectionFlow.value = null
		sectionBackStack.clear()
	}

	private fun newRootScreenInSection(screen: FragmentScreen) {
		val currentSectionName = if (sectionBackStack.isEmpty()) {
			currentSectionFlow.value = SectionNames.HOME
			SectionNames.HOME
		} else {
			SectionNames.valueOf(sectionBackStack.last().screenKey)
		}
		sectionRouterMap[currentSectionName]?.newRootScreen(screen)
	}

	private fun findSectionIndex(screen: SectionScreen): Int? =
		sectionBackStack.indexOfLast { it.screenKey == screen.screenKey }.takeIf { it >= 0 }

	private fun popToRootInSection(sectionName: SectionNames) {
		val root = sectionRoots[sectionName] ?: return
		sectionRouterMap[sectionName]?.newRootScreen(root())
	}

	enum class HostStatus {
		EMPTY,
		NOT_EMPTY,
	}

	private companion object {

		const val MIN_SCREEN_IN_BACK_STACK = 1
	}
}
