package com.nmichail.meridian.core.navigation.navigator

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.androidx.AppNavigator

class ScreenNavigator(
	activity: FragmentActivity,
	containerId: Int,
	fragmentManager: FragmentManager = activity.supportFragmentManager,
) : AppNavigator(activity, containerId, fragmentManager)