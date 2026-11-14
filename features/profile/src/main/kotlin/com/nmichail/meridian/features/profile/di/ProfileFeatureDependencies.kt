package com.nmichail.meridian.features.profile.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.profile.presentation.ProfileRouter

interface ProfileFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): ProfileRouter
}

class ProfileFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: ProfileRouter,
) : ProfileFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): ProfileRouter = router
}
