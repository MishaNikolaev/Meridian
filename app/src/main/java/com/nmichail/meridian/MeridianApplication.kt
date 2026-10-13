package com.nmichail.meridian

import android.app.Application
import com.nmichail.meridian.core.network.NetworkConfig
import com.nmichail.meridian.di.AppComponent
import com.nmichail.meridian.di.DaggerAppComponent
import com.nmichail.meridian.di.installComponents
import javax.inject.Inject

class MeridianApplication : Application() {

	lateinit var component: AppComponent
		private set

	@Inject
	lateinit var networkConfig: NetworkConfig

	override fun onCreate() {
		super.onCreate()
		component = DaggerAppComponent.factory().create(this)
		component.installComponents()
		component.inject(this)
		check(networkConfig.baseUrl.isNotBlank())
	}
}
