package com.nmichail.meridian.features.authorization.splash.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.splash.presentation.SplashRouter

interface SplashFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SplashRouter
}

class SplashFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: SplashRouter,
) : SplashFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): SplashRouter = router
}