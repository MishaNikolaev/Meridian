package com.nmichail.meridian.features.authorization.signin.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object SignInComponentHolder : ComponentHolder<SignInFeatureApi, SignInFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		SignInFeatureApi,
		SignInFeatureDependencies,
		SignInComponent,
		> { dependencies ->
		DaggerSignInComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> SignInFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): SignInFeatureApi = componentHolderDelegate.get()
}
