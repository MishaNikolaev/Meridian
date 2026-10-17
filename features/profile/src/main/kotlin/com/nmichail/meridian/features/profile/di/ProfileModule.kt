package com.nmichail.meridian.features.profile.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.profile.presentation.ProfileViewModel
import com.nmichail.meridian.shared.session.di.AuthorizationModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [AuthorizationModule::class])
abstract class ProfileModule {

	@Binds
	@IntoMap
	@ViewModelKey(ProfileViewModel::class)
	abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel
}
