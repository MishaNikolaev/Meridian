package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter

interface SplashFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SplashRouter
}

fun splashFeatureDependencies(
	core: CoreApi,
	router: SplashRouter,
): SplashFeatureDependencies = object : DependencyHolder0<SplashFeatureDependencies>() {
	override val block: (BaseDependencyHolder<SplashFeatureDependencies>) -> SplashFeatureDependencies = { holder ->
		object : SplashFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): SplashRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies