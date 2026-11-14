package com.nmichail.meridian.features.search.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.search.presentation.SearchRouter

interface SearchFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): SearchRouter
}

class SearchFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: SearchRouter,
) : SearchFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): SearchRouter = router
}