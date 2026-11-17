package com.nmichail.meridian.features.profile.about.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object AboutComponentHolder : ComponentHolder<AboutFeatureApi, AboutFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		AboutFeatureApi,
		AboutFeatureDependencies,
		AboutComponent,
		> { dependencies ->
		DaggerAboutComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> AboutFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): AboutFeatureApi = componentHolderDelegate.get()
}