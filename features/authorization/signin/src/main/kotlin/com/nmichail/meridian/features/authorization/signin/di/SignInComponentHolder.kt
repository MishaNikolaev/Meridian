package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.ComponentHolder

object SignInComponentHolder : ComponentHolder<SignInFeatureApi> {

	private var component: SignInFeatureApi? = null

	fun init(dependencies: SignInFeatureDependencies) {
		if (component == null) {
			component = DaggerSignInComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): SignInFeatureApi =
		checkNotNull(component) { "SignInComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
