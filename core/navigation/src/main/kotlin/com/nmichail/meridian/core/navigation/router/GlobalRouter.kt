package com.nmichail.meridian.core.navigation.router

import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.nmichail.meridian.core.navigation.section.SectionNames
import kotlinx.coroutines.flow.StateFlow

interface GlobalRouter {

	val currentSection: StateFlow<SectionNames?>

	fun newRootScreen(fragmentScreen: FragmentScreen)

	fun navigateTo(screen: Screen)

	fun replace(fragmentScreen: FragmentScreen)

	fun navigateBack()

	fun openLink(url: String)
}
