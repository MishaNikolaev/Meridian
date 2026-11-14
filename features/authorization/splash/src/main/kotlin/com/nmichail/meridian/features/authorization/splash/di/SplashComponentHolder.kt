package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object SplashComponentHolder : ComponentHolder<SplashFeatureApi, SplashFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		SplashFeatureApi,
		SplashFeatureDependencies,
		SplashComponent,
		> { dependencies ->
		DaggerSplashComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> SplashFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): SplashFeatureApi = componentHolderDelegate.get()
}
