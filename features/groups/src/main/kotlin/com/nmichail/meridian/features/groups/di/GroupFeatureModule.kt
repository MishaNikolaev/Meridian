package com.nmichail.meridian.features.groups.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.component.photopicker.di.PhotoModule
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.groups.presentation.GroupViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [PhotoModule::class])
abstract class GroupFeatureModule {
	@Binds
	@IntoMap
	@ViewModelKey(GroupViewModel::class)
	abstract fun bindGroupViewModel(viewModel: GroupViewModel): ViewModel
}