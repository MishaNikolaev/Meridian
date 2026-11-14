package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceRouter
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class],
	modules = [WorkspaceModule::class, ViewModelModule::class],
)
internal interface WorkspaceComponent : WorkspaceFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi, @BindsInstance router: WorkspaceRouter): WorkspaceComponent
	}
}
