package com.nmichail.meridian.features.person.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.person.presentation.PersonRouter

interface PersonFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): PersonRouter
}

class PersonFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: PersonRouter,
) : PersonFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): PersonRouter = router
}