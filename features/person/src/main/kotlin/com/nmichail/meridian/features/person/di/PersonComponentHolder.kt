package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object PersonComponentHolder : ComponentHolder<PersonFeatureApi, PersonFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		PersonFeatureApi,
		PersonFeatureDependencies,
		PersonComponent,
		> { dependencies ->
		DaggerPersonComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> PersonFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): PersonFeatureApi = componentHolderDelegate.get()
}
