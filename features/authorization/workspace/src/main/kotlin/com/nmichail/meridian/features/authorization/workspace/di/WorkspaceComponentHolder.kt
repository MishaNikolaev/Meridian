package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.ComponentHolder

object WorkspaceComponentHolder : ComponentHolder<WorkspaceFeatureApi> {

	private var component: WorkspaceFeatureApi? = null

	fun init(dependencies: WorkspaceFeatureDependencies) {
		if (component == null) {
			component = DaggerWorkspaceComponent.factory().create(dependencies.core(), dependencies.router())
		}
	}

	override fun get(): WorkspaceFeatureApi =
		checkNotNull(component) { "WorkspaceComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
