package com.nmichail.meridian

import android.app.Application
import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.di.AppComponent
import com.nmichail.meridian.di.DaggerAppComponent
import com.nmichail.meridian.di.FeatureComponents
import javax.inject.Inject

class MeridianApplication : Application() {

	lateinit var component: AppComponent
		private set

	@Inject
	lateinit var networkConfig: NetworkConfig

	@Inject
	lateinit var featureComponents: FeatureComponents

	override fun onCreate() {
		super.onCreate()
		component = DaggerAppComponent.factory().create(this)
		component.inject(this)
		featureComponents.install(component)
		check(networkConfig.baseUrl.isNotBlank())
	}
}
