package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter

interface SignInFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SignInRouter
}

class SignInFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: SignInRouter,
) : SignInFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): SignInRouter = router
}