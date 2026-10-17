package com.nmichail.meridian.features.authorization.workspace.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceViewModel
import com.nmichail.meridian.shared.session.di.AuthorizationModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [AuthorizationModule::class])
abstract class WorkspaceModule {

	@Binds
	@IntoMap
	@ViewModelKey(WorkspaceViewModel::class)
	abstract fun bindWorkspaceViewModel(viewModel: WorkspaceViewModel): ViewModel
}
