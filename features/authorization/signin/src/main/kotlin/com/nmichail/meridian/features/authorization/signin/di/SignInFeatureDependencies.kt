package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.signin.presentation.SignInRouter

interface SignInFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SignInRouter
}

fun signInFeatureDependencies(
	core: CoreApi,
	router: SignInRouter,
): SignInFeatureDependencies = object : DependencyHolder0<SignInFeatureDependencies>() {

	override val block: (BaseDependencyHolder<SignInFeatureDependencies>) -> SignInFeatureDependencies = { holder ->
		object : SignInFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): SignInRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies