package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.search.presentation.SearchRouter

interface SearchFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SearchRouter
}

fun searchFeatureDependencies(
	core: CoreApi,
	router: SearchRouter,
): SearchFeatureDependencies = object : DependencyHolder0<SearchFeatureDependencies>() {
	override val block: (BaseDependencyHolder<SearchFeatureDependencies>) -> SearchFeatureDependencies = { holder ->
		object : SearchFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): SearchRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies
