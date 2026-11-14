package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object SearchComponentHolder : ComponentHolder<SearchFeatureApi, SearchFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		SearchFeatureApi,
		SearchFeatureDependencies,
		SearchComponent,
		> { dependencies ->
		DaggerSearchComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> SearchFeatureDependencies)? by componentHolderDelegate::dependencyProvider

	override fun get(): SearchFeatureApi = componentHolderDelegate.get()
}