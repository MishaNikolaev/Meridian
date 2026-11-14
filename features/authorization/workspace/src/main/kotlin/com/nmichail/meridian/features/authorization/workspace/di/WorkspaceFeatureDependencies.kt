package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter

interface WorkspaceFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): WorkspaceRouter
}

class WorkspaceFeatureDependenciesImpl(
	private val core: CoreApi,
	private val router: WorkspaceRouter,
) : WorkspaceFeatureDependencies {

	override fun core(): CoreApi = core

	override fun router(): WorkspaceRouter = router
}