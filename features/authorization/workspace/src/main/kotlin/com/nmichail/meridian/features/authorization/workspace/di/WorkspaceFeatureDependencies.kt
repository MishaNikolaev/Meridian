package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter

interface WorkspaceFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun router(): WorkspaceRouter
}

fun workspaceFeatureDependencies(
	core: CoreApi,
	router: WorkspaceRouter,
): WorkspaceFeatureDependencies = object : DependencyHolder0<WorkspaceFeatureDependencies>() {
	override val block: (BaseDependencyHolder<WorkspaceFeatureDependencies>) -> WorkspaceFeatureDependencies = { holder ->
		object : WorkspaceFeatureDependencies {
			override fun core(): CoreApi = core

			override fun router(): WorkspaceRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies