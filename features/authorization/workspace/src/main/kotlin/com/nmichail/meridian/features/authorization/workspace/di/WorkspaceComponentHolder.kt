package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter

object WorkspaceComponentHolder : ComponentHolder<WorkspaceFeatureApi> {

	private var component: WorkspaceFeatureApi? = null

	fun init(core: CoreApi, router: WorkspaceRouter) {
		if (component == null) {
			component = DaggerWorkspaceComponent.factory().create(core, router)
		}
	}

	override fun get(): WorkspaceFeatureApi =
		checkNotNull(component) { "WorkspaceComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
