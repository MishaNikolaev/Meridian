package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object ProfileComponentHolder : ComponentHolder<ProfileFeatureApi, ProfileFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		ProfileFeatureApi,
		ProfileFeatureDependencies,
		ProfileComponent,
		> { dependencies ->
		DaggerProfileComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> ProfileFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): ProfileFeatureApi = componentHolderDelegate.get()
}
