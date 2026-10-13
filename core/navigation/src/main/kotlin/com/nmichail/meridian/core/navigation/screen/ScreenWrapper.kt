package com.nmichail.meridian.core.navigation.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen

abstract class ScreenWrapper(
	private val key: String,
	private val fragmentClass: Class<out Fragment>,
) : FragmentScreen {

	override val screenKey: String
		get() = key

	override fun createFragment(factory: FragmentFactory): Fragment =
		factory.instantiate(fragmentClass.classLoader!!, fragmentClass.name)
}
