package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.ComponentHolder

object SplashComponentHolder : ComponentHolder<SplashFeatureApi> {

	private var component: SplashFeatureApi? = null

	fun init(dependencies: SplashFeatureDependencies) {
		if (component == null) {
			component = DaggerSplashComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): SplashFeatureApi =
		checkNotNull(component) { "SplashComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
