package com.nmichail.meridian.features.groups.info.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.component.photopicker.di.PhotoModule
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoViewModel
import com.nmichail.meridian.shared.organization.di.OrganizationModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [PhotoModule::class, OrganizationModule::class])
abstract class GroupInfoFeatureModule {

	@Binds
	@IntoMap
	@ViewModelKey(GroupInfoViewModel::class)
	abstract fun bindGroupInfoViewModel(viewModel: GroupInfoViewModel): ViewModel
}