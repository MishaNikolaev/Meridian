package com.nmichail.meridian.core.navigation.navigator

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen

class KeepStateNavigator(
	activity: FragmentActivity,
	containerId: Int,
	manager: FragmentManager,
) : AppNavigator(activity, containerId, manager) {

	override fun commitNewFragmentScreen(screen: FragmentScreen, addToBackStack: Boolean) {
		val fragmentTransaction = fragmentManager.beginTransaction()
		fragmentManager.primaryNavigationFragment?.let(fragmentTransaction::hide)
		var fragment = fragmentManager.findFragmentByTag(screen.screenKey)
		if (fragment == null) {
			fragment = screen.createFragment(fragmentFactory)
			fragmentTransaction.add(containerId, fragment, screen.screenKey)
		} else {
			fragmentTransaction.show(fragment)
		}
		fragmentTransaction.setPrimaryNavigationFragment(fragment)
		fragmentTransaction.setReorderingAllowed(true)
		fragmentTransaction.commit()
	}
}
