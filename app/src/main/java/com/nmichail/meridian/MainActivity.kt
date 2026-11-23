package com.nmichail.meridian

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.terrakok.cicerone.NavigatorHolder
import com.nmichail.meridian.core.navigation.di.FullScreen
import com.nmichail.meridian.core.navigation.navigator.ScreenNavigator
import com.nmichail.meridian.core.network.session.SessionEvents
import com.nmichail.meridian.di.ActivityComponent
import com.nmichail.meridian.di.ChatSessionHolder
import com.nmichail.meridian.navigation.router.main.MainRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeHolder
import com.nmichail.meridian.features.calls.ring.IncomingRing
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

	@Inject
	lateinit var router: MainRouter

	@Inject
	lateinit var sessionEvents: SessionEvents

	@Inject
	lateinit var incomingRing: IncomingRing

	@Inject
	@FullScreen
	lateinit var navigatorHolder: NavigatorHolder

	private val navigator by lazy { ScreenNavigator(this, R.id.container) }

	private lateinit var activityComponent: ActivityComponent

	private var rings: Job? = null

	fun ring(): IncomingRing = incomingRing

	override fun onCreate(savedInstanceState: Bundle?) {
		val appComponent = (application as MeridianApplication).component
		activityComponent = appComponent.activityComponent().create(activity = this, owner = this)
		activityComponent.inject(this)
		val sessionOpen = ChatSessionHolder.current() != null
		super.onCreate(if (sessionOpen) savedInstanceState else null)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		if (!sessionOpen || savedInstanceState == null) {
			router.navigateToSplash()
		}
		lifecycleScope.launch {
			sessionEvents.expired.collect {
				router.navigateToSignIn()
			}
		}
		watchRings()
		ring().onIntent(intent)
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		setIntent(intent)
		ring().onIntent(intent)
	}

	private fun watchRings() {
		val app = application as MeridianApplication
		val realtime = ChatRealtimeHolder.get(app.component).chatRealtime()
		realtime.ensureStarted()
		rings = lifecycleScope.launch {
			realtime.calls.collect { notice ->
				ring().onNotice(notice)
			}
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
