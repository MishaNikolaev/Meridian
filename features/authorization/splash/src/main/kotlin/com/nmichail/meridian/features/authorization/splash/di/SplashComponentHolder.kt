package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter

object SplashComponentHolder : ComponentHolder<SplashFeatureApi> {

	private var component: SplashFeatureApi? = null

	fun init(core: CoreApi, router: SplashRouter) {
		if (component == null) {
			component = DaggerSplashComponent.factory().create(core, router)
		}
	}

	override fun get(): SplashFeatureApi =
		checkNotNull(component) { "SplashComponent was not initialized" }

	override fun reset() {
		component = null
	}
}