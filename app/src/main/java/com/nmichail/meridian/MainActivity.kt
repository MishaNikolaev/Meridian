package com.nmichail.meridian

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.github.terrakok.cicerone.NavigatorHolder
import com.nmichail.meridian.core.navigation.di.FullScreen
import com.nmichail.meridian.core.navigation.navigator.ScreenNavigator
import com.nmichail.meridian.di.ActivityComponent
import com.nmichail.meridian.navigation.router.main.MainRouter
import javax.inject.Inject

class MainActivity : FragmentActivity() {

	@Inject
	lateinit var router: MainRouter

	@Inject
	@FullScreen
	lateinit var navigatorHolder: NavigatorHolder

	private val navigator by lazy { ScreenNavigator(this, R.id.container) }

	private lateinit var activityComponent: ActivityComponent

	override fun onCreate(savedInstanceState: Bundle?) {
		val appComponent = (application as MeridianApplication).component
		activityComponent = appComponent.activityComponent().create()
		activityComponent.inject(this)
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		if (savedInstanceState == null) {
			router.navigateToHome()
		}
	}

	override fun onResume() {
		super.onResume()
		navigatorHolder.setNavigator(navigator)
	}

	override fun onPause() {
		super.onPause()
		navigatorHolder.removeNavigator()
	}
}
