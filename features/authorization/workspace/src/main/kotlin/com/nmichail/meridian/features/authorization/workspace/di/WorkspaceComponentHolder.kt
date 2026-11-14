package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.di.ComponentHolderDelegate

object WorkspaceComponentHolder :
	ComponentHolder<WorkspaceFeatureApi, WorkspaceFeatureDependencies> {

	private val componentHolderDelegate = ComponentHolderDelegate<
		WorkspaceFeatureApi,
		WorkspaceFeatureDependencies,
		WorkspaceComponent,
		> { dependencies ->
		DaggerWorkspaceComponent.factory().create(dependencies.core(), dependencies.router())
	}

	override var dependencyProvider: (() -> WorkspaceFeatureDependencies)?
		by componentHolderDelegate::dependencyProvider

	override fun get(): WorkspaceFeatureApi = componentHolderDelegate.get()
}
