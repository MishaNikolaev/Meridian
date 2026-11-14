package com.nmichail.meridian.features.registration.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.registration.presentation.RegistrationRouter

interface RegistrationFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): RegistrationRouter
}

class RegistrationFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: RegistrationRouter,
) : RegistrationFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): RegistrationRouter = router
}